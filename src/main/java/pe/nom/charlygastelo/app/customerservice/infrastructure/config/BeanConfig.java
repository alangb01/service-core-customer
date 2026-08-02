package pe.nom.charlygastelo.app.customerservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.cache.RedisCustomerCacheAdapter;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.CustomerReactiveRepository;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.CustomerRepositoryAdapter;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;

@Configuration
public class BeanConfig {

//    @Bean
//    @Primary
//    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
//            ReactiveRedisConnectionFactory factory) {
//
//        RedisSerializationContext<String, String> context =
//                RedisSerializationContext.<String, String>newSerializationContext(
//                        new StringRedisSerializer()
//                ).value(new StringRedisSerializer()).build();
//
//        return new ReactiveRedisTemplate<>(factory, context);
//    }

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

}
