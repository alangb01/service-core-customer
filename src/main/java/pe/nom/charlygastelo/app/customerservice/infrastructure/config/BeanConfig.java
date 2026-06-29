package pe.nom.charlygastelo.app.customerservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import pe.nom.charlygastelo.app.customerservice.application.usecase.*;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.CustomerRepositoryAdapter;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.ReactiveCustomerRepository;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import pe.nom.charlygastelo.app.customerservice.infrastructure.cache.RedisCustomerCacheAdapter;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.CustomerEventProducer;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper.CustomerEventMapper;


@Configuration
public class BeanConfig {

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        RedisSerializationContext<String, String> context =
                RedisSerializationContext.<String, String>newSerializationContext(
                        new StringRedisSerializer()
                ).value(new StringRedisSerializer()).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public CustomerCachePort customerCachePort(
            ReactiveRedisTemplate<String, String> redis,
            ObjectMapper mapper) {

        return new RedisCustomerCacheAdapter(redis, mapper);
    }

    @Bean
    public CustomerRepositoryPort customerRepositoryPort(ReactiveCustomerRepository repository,
                                                         PersistenceMapper mapper) {
        return new CustomerRepositoryAdapter(repository, mapper);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(CustomerRepositoryPort repository, CustomerEventProducerPort producer, CustomerCachePort cache) {
        return new CreateCustomerUseCase(repository, producer, cache);
    }

    @Bean
    public GetCustomerUseCase getCustomerUseCase(CustomerRepositoryPort repository, CustomerCachePort cache) {
        return new GetCustomerUseCase(repository, cache);
    }

    @Bean
    public ListCustomersUseCase listCustomersUseCase(CustomerRepositoryPort repository, CustomerCachePort cache) {
        return new ListCustomersUseCase(repository, cache);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(CustomerRepositoryPort repository, CustomerCachePort cache, CustomerEventProducer producer) {
        return new UpdateCustomerUseCase(repository, cache, producer);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(CustomerRepositoryPort repository, CustomerCachePort cache, CustomerEventProducer producer) {
        return new DeleteCustomerUseCase(repository, cache, producer);
    }
}
