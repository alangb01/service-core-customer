package pe.nom.charlygastelo.app.customerservice.infrastructure.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerResponseEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerResponseProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer serializer;

    @Value("${topic.customer-response}")
    private String customerResponseTopic;

    public void publish(String correlationId, CustomerResponseEvent event) {
        try {
            log.info("[CUSTOMER-RESPONSE] Preparing to publish event. correlationId={}, found={}",
                    correlationId, event.getFound());

            log.debug("[CUSTOMER-RESPONSE] Serializing CustomerResponseEvent. correlationId={}, event={}",
                    correlationId, event);

            String payload = serializer.serialize(event);

            log.debug("[CUSTOMER-RESPONSE] Payload serialized successfully. correlationId={}, payload={}",
                    correlationId, payload);

            kafkaTemplate.send(customerResponseTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("[CUSTOMER-RESPONSE] Error sending event. correlationId={}, reason={}",
                                    correlationId, error.getMessage(), error);
                            return;
                        }

                        log.info("[CUSTOMER-RESPONSE] Event published successfully. correlationId={}, found={}, partition={}, offset={}",
                                correlationId,
                                event.getFound(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    });

        } catch (Exception e) {
            log.error("[CUSTOMER-RESPONSE] Error serializing CustomerResponseEvent. correlationId={}, reason={}",
                    correlationId, e.getMessage(), e);
        }
    }
}
