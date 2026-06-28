package pe.nom.charlygastelo.app.customerservice.infrastructure.events;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper.CustomerEventMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventProducer implements CustomerEventProducerPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer serializer;
    private final CustomerEventMapper mapper;

    @Value("${topic.customer-created}")
    private String customerCreatedTopic;

    @Value("${topic.customer-updated}")
    private String customerUpdatedTopic;

    @Override
    public Completable publishCustomerCreated(Customer customer) {
        return publish(customerCreatedTopic, customer.id(), mapper.toCustomerCreatedEvent(customer));
    }

    @Override
    public Completable publishCustomerUpdated(Customer customer) {
        return publish(customerUpdatedTopic, customer.id(), mapper.toCustomerUpdatedEvent(customer));
    }

    private Completable publish(String topic, String key, SpecificRecordBase event) {
        return Completable.create(emitter -> {
            try {
                String payload = serializer.serialize(event);

                kafkaTemplate.send(topic, key, payload)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                log.error("Error publishing customer event. topic={}, key={}, reason={}",
                                        topic, key, error.getMessage(), error);
                                emitter.onError(error);
                                return;
                            }

                            log.info("Customer event published successfully. topic={}, key={}",
                                    topic, key);
                            emitter.onComplete();
                        });

            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}