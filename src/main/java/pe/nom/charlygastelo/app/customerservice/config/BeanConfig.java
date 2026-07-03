package pe.nom.charlygastelo.app.customerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import pe.nom.charlygastelo.app.customerservice.adapter.out.cache.RedisCustomerCacheAdapter;
import pe.nom.charlygastelo.app.customerservice.adapter.out.persistence.CustomerReactiveRepository;
import pe.nom.charlygastelo.app.customerservice.adapter.out.persistence.CustomerRepositoryAdapter;
import pe.nom.charlygastelo.app.customerservice.adapter.out.persistence.mapper.PersistenceMapper;
import pe.nom.charlygastelo.app.customerservice.application.usecase.*;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

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
    public CustomerRepositoryPort customerRepositoryPort(CustomerReactiveRepository repository,
                                                         PersistenceMapper mapper) {
        return new CustomerRepositoryAdapter(repository, mapper);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(CustomerRepositoryPort repository,
                                                       CustomerEventProducerPort producer,
                                                       CustomerCachePort cache) {
        return new CreateCustomerUseCase(repository, cache, producer);
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
    public UpdateCustomerUseCase updateCustomerUseCase(
            CustomerRepositoryPort repository,
            CustomerCachePort cache,
            CustomerEventProducerPort producer) {
        return new UpdateCustomerUseCase(repository, cache, producer);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(CustomerRepositoryPort repository,
                                                       CustomerCachePort cache,
                                                       CustomerEventProducerPort producer) {
        return new DeleteCustomerUseCase(repository, cache, producer);
    }
}
