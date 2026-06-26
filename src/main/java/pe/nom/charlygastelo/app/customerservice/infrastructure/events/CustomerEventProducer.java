package pe.nom.charlygastelo.app.customerservice.infrastructure.events;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper.CustomerEventMapper;


@RequiredArgsConstructor
@Slf4j
public class CustomerEventProducer implements CustomerEventPort {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CustomerEventMapper mapper;

    @Value("${topic.customer-created}")
    private String accountCreatedTopic;

    @Value("${topic.customer-updated}")
    private String accountUpdatedTopic;

    @Override
    public Completable publishCustomerCreatedEvent(Customer customer) {
        var event = mapper.toCustomerCreatedEvent(customer);

        return publish(accountCreatedTopic, customer.id(), event);
    }

    @Override
    public Completable publishCustomerUpdatedEvent(Customer customer) {
        var event = mapper.toCustomerCreatedEvent(customer);

        return publish(accountUpdatedTopic, customer.id(), event);
    }

    private Completable publish(String topic, String key, Object event) {
        return Completable.create(emitter -> {
            try {
                kafkaTemplate.send(
                        topic,
                        key,
                        event.toString()
                ).whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event sent to topic {} with key {}", topic, key);
                        emitter.onComplete();
                    } else {
                        log.error("Error sending event to topic {}: {}", topic, ex.getMessage());
                        emitter.onError(ex);
                    }
                });
            }
            catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
