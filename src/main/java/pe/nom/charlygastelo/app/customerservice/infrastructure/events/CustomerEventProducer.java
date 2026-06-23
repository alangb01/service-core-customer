package pe.nom.charlygastelo.app.customerservice.infrastructure.events;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper.CustomerEventMapper;


public class CustomerEventProducer implements CustomerEventPort {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CustomerEventMapper mapper;

    @Value("${topic.customer-created}")
    private String accountCreatedTopic;

    @Value("${topic.customer-updated}")
    private String accountUpdatedTopic;

    public CustomerEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                 CustomerEventMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public Completable publishCustomerCreatedEvent(Customer customer) {
        var event = mapper.toCustomerCreatedEvent(customer);

        return Completable.create(emitter -> {
            try {
                kafkaTemplate.send(
                        accountCreatedTopic,
                        customer.id(),
                        event.toString()
                );
            }
            catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    @Override
    public Completable publishCustomerUpdatedEvent(Customer customer) {
        var event = mapper.toCustomerCreatedEvent(customer);

        return Completable.create(emitter -> {
            try {
                kafkaTemplate.send(
                        accountUpdatedTopic,
                        customer.id(),
                        event.toString()
                );
            }
            catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

}
