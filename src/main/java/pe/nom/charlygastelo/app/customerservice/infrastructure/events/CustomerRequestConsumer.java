package pe.nom.charlygastelo.app.customerservice.infrastructure.events;

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

    private final AvroJsonDeserializer deserializer;
    private final CustomerRepositoryPort repository;
    private final CustomerResponseProducer responseProducer;
    private final CustomerEventMapper mapper;

    @KafkaListener(topics = "${topic.customer-request}", groupId = "customer-service")
    public void consume(String message) {
        try {
            CustomerRequestEvent event = deserializer.deserialize(
                    message,
                    CustomerRequestEvent.class,
                    CustomerRequestEvent.getClassSchema()
            );

            String correlationId = event.getCorrelationId().toString();
            String customerId = event.getCustomerId().toString();

            log.info("CustomerRequestEvent received. correlationId={}, customerId={}",
                    correlationId, customerId);

            repository.findById(customerId)
                    .subscribe(
                            customer -> responseProducer.publish(
                                    correlationId,
                                    mapper.toCustomerResponseEvent(customer, correlationId)
                            ),
                            error -> {
                                log.error("Error searching customer. correlationId={}, customerId={}, reason={}",
                                        correlationId, customerId, error.getMessage(), error);

                                responseProducer.publish(
                                        correlationId,
                                        mapper.toCustomerNotFoundEvent(customerId, correlationId)
                                );
                            },
                            () -> {
                                log.warn("Customer not found. correlationId={}, customerId={}",
                                        correlationId, customerId);

                                responseProducer.publish(
                                        correlationId,
                                        mapper.toCustomerNotFoundEvent(customerId, correlationId)
                                );
                            }
                    );

        } catch (Exception e) {
            log.error("Error processing CustomerRequestEvent", e);
        }
    }
}