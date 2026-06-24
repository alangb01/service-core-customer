package pe.nom.charlygastelo.app.customerservice.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.cache.exception.CustomerCacheException;

@RequiredArgsConstructor
public class RedisCustomerCacheAdapter implements CustomerCachePort {

    private final ReactiveRedisTemplate<String, String> redis;
    private final ObjectMapper mapper;

    private static final String KEY_ID = "customer:id:";
    private static final String KEY_DOC = "customer:doc:";

    @Override
    public Maybe<Customer> getById(String id) {
        return Maybe.fromPublisher(
                        redis.opsForValue().get(KEY_ID + id)
                )
                .flatMap(this::parse)
                .onErrorResumeNext(e ->
                        Maybe.error(new CustomerCacheException("Redis error", e))
                );
    }

    @Override
    public Maybe<Customer> getByDocument(String type, String document) {
        return Maybe.fromPublisher(
                        redis.opsForValue().get(KEY_DOC + type + ":" + document)
                )
                .flatMap(this::parse)
                .onErrorResumeNext(e ->
                        Maybe.error(new CustomerCacheException("Redis error", e))
                );
    }

    @Override
    public Completable save(Customer customer) {
        try {
            String json = mapper.writeValueAsString(customer);

            return Completable.fromPublisher(
                    redis.opsForValue().set(KEY_ID + customer.id(), json)
            ).andThen(
                    Completable.fromPublisher(
                            redis.opsForValue().set(
                                    KEY_DOC + customer.documentType() + ":" + customer.documentNumber(),
                                    json
                            )
                    )
            );

        } catch (Exception e) {
            return Completable.error(new CustomerCacheException("Error serializing customer", e));
        }
    }

    @Override
    public Completable delete(String id) {
        return Completable.fromPublisher(
                redis.opsForValue().delete(KEY_ID + id)
        );
    }

    private Maybe<Customer> parse(String json) {
        try {
            return json == null
                    ? Maybe.empty()
                    : Maybe.just(mapper.readValue(json, Customer.class));
        } catch (Exception e) {
            return Maybe.error(new CustomerCacheException("Error parsing cache", e));
        }
    }
}
