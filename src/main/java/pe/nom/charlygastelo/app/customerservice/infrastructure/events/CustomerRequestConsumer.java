package pe.nom.charlygastelo.app.customerservice.infrastructure.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper.CustomerEventMapper;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRequestConsumer {

    private final CustomerRepositoryPort repository;
    private final CustomerResponseProducer producer;
    private final CustomerEventMapper mapper;
    private final AvroJsonDeserializer avroJsonDeserializer;

    @KafkaListener(topics = "${topic.customer-request}", groupId = "customer-service")
    public void consumeCustomerRequest(String message) {
        try {
            CustomerRequestEvent event =
                    avroJsonDeserializer.deserialize(
                            message,
                            CustomerRequestEvent.class,
                            CustomerRequestEvent.getClassSchema()
                    );

            String correlationId = event.getCorrelationId().toString();
            String customerId = event.getCustomerId().toString();

            repository.findById(customerId)
                    .switchIfEmpty(Single.error(new RuntimeException("Customer not found")))
                    .subscribe(
                            customer -> producer.publish(
                                    correlationId,
                                    mapper.toCustomerResponseEvent(customer, correlationId)
                            ),
                            error -> producer.publish(
                                    correlationId,
                                    mapper.toCustomerNotFoundEvent(customerId, correlationId)
                            )
                    );

        } catch (Exception e) {
            log.error("Error processing CustomerRequestEvent", e);
        }
    }
}