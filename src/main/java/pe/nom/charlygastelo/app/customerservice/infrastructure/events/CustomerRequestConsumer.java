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
        log.info("[CUSTOMER-REQUEST] Message received from Kafka.");

        try {
            log.debug("[CUSTOMER-REQUEST] Deserializing CustomerRequestEvent. rawMessage={}", message);

            CustomerRequestEvent event = deserializer.deserialize(
                    message,
                    CustomerRequestEvent.class,
                    CustomerRequestEvent.getClassSchema()
            );

            String correlationId = event.getCorrelationId().toString();
            String customerId = event.getCustomerId().toString();

            log.info("[CUSTOMER-REQUEST] Event deserialized successfully. correlationId={}, customerId={}",
                    correlationId, customerId);

            repository.findById(customerId)
                    .subscribe(
                            customer -> {
                                log.info("[CUSTOMER-REQUEST] Customer found. correlationId={}, customerId={}",
                                        correlationId, customerId);

                                responseProducer.publish(
                                        correlationId,
                                        mapper.toCustomerResponseEvent(customer, correlationId)
                                );

                                log.info("[CUSTOMER-RESPONSE] CustomerResponseEvent published. correlationId={}, customerId={}",
                                        correlationId, customerId);
                            },
                            error -> {
                                log.error("[CUSTOMER-REQUEST] Error searching customer. correlationId={}, customerId={}, reason={}",
                                        correlationId, customerId, error.getMessage(), error);

                                responseProducer.publish(
                                        correlationId,
                                        mapper.toCustomerNotFoundEvent(customerId, correlationId)
                                );

                                log.warn("[CUSTOMER-RESPONSE] CustomerNotFoundEvent published due to error. correlationId={}, customerId={}",
                                        correlationId, customerId);
                            },
                            () -> {
                                log.warn("[CUSTOMER-REQUEST] Customer not found. correlationId={}, customerId={}",
                                        correlationId, customerId);

                                responseProducer.publish(
                                        correlationId,
                                        mapper.toCustomerNotFoundEvent(customerId, correlationId)
                                );

                                log.info("[CUSTOMER-RESPONSE] CustomerNotFoundEvent published. correlationId={}, customerId={}",
                                        correlationId, customerId);
                            }
                    );

        } catch (Exception e) {
            log.error("[CUSTOMER-REQUEST] Fatal error processing CustomerRequestEvent. reason={}", e.getMessage(), e);
        }
    }
}
