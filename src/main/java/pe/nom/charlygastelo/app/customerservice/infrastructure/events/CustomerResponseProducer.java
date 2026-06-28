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
            String payload = serializer.serialize(event);

            kafkaTemplate.send(customerResponseTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Error publishing CustomerResponseEvent. correlationId={}, reason={}",
                                    correlationId, error.getMessage(), error);
                            return;
                        }

                        log.info("CustomerResponseEvent published. correlationId={}, found={}",
                                correlationId, event.getFound());
                    });

        } catch (Exception e) {
            log.error("Error serializing CustomerResponseEvent. correlationId={}",
                    correlationId, e);
        }
    }
}