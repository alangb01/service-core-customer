package pe.nom.charlygastelo.app.customerservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.KafkaTemplate;
import pe.nom.charlygastelo.app.customerservice.application.usecase.*;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.CustomerRepositoryAdapter;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.ReactiveCustomerRepository;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.CustomerEventProducer;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper.CustomerEventMapper;


@Configuration
public class BeanConfig {

    @Bean
    public CustomerRepositoryPort customerRepositoryPort(ReactiveCustomerRepository repository,
                                                         PersistenceMapper mapper) {
        return new CustomerRepositoryAdapter(repository, mapper);
    }


    @Bean
    public CustomerEventPort customerEventPort(KafkaTemplate<String, String> kafkaTemplate,
                                               CustomerEventMapper mapper) {
        return new CustomerEventProducer(kafkaTemplate, mapper);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(CustomerRepositoryPort repository, CustomerEventPort producer) {
        return new CreateCustomerUseCase(repository, producer);
    }

    @Bean
    public GetCustomerUseCase getCustomerUseCase(CustomerRepositoryPort repository) {
        return new GetCustomerUseCase(repository);
    }

    @Bean
    public ListCustomersUseCase listCustomersUseCase(CustomerRepositoryPort repository) {
        return new ListCustomersUseCase(repository);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(CustomerRepositoryPort repository) {
        return new UpdateCustomerUseCase(repository);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(CustomerRepositoryPort repository) {
        return new DeleteCustomerUseCase(repository);
    }
}