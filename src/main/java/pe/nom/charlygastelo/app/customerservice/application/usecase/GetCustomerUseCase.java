package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.exception.CustomerRepositoryException;

@RequiredArgsConstructor
@Slf4j
public class GetCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerCachePort cache;

    public Maybe<Customer> byId(String id) {
        log.info("[CUSTOMER-GET] Searching customer by ID {}", id);

        return safeCacheGetById(id)
                .switchIfEmpty(
                        customerRepository.findById(id)
                                .flatMap(customer ->
                                        safeCacheSave(customer)
                                                .andThen(Maybe.just(customer))
                                )
                                .onErrorResumeNext(e -> {
                                    log.error("[CUSTOMER-GET] Mongo error retrieving customer {}: {}",
                                            id, e.getMessage(), e);
                                    return Maybe.error(new CustomerRepositoryException("Error accessing Mongo", e));
                                })
                )
                .switchIfEmpty(
                        Maybe.error(new CustomerNotFoundException("Customer not found: " + id))
                );
    }

    public Maybe<Customer> byDocument(String type, String number) {
        log.info("[CUSTOMER-GET] Searching customer by document {} {}", type, number);

        return safeCacheGetByDocument(type, number)
                .switchIfEmpty(
                        customerRepository.findByDocument(type, number)
                                .flatMap(customer ->
                                        safeCacheSave(customer)
                                                .andThen(Maybe.just(customer))
                                )
                                .onErrorResumeNext(e -> {
                                    log.error("[CUSTOMER-GET] Mongo error retrieving customer {}-{}: {}",
                                            type, number, e.getMessage(), e);
                                    return Maybe.error(new CustomerRepositoryException("Error accessing Mongo", e));
                                })
                )
                .switchIfEmpty(
                        Maybe.error(new CustomerNotFoundException(
                                "Customer not found with " + type + " " + number
                        ))
                );
    }

    private Maybe<Customer> safeCacheGetById(String id) {
        Maybe<Customer> op = cache.getById(id);

        if (op == null) {
            log.warn("[CUSTOMER-GET] Cache getById returned null. customerId={}", id);
            return Maybe.empty();
        }

        return op
                .doOnSuccess(c ->
                        log.info("[CUSTOMER-GET] Customer {} found in cache", id))
                .onErrorComplete(e -> {
                    log.warn("[CUSTOMER-GET] Cache getById failed. Falling back to MongoDB. customerId={}, reason={}",
                            id, e.getMessage());
                    return true;
                });
    }

    private Maybe<Customer> safeCacheGetByDocument(String type, String number) {
        Maybe<Customer> op = cache.getByDocument(type, number);

        if (op == null) {
            log.warn("[CUSTOMER-GET] Cache getByDocument returned null. documentType={}, documentNumber={}",
                    type, number);
            return Maybe.empty();
        }

        return op
                .doOnSuccess(c ->
                        log.info("[CUSTOMER-GET] Customer {}-{} found in cache", type, number))
                .onErrorComplete(e -> {
                    log.warn("[CUSTOMER-GET] Cache getByDocument failed. Falling back to MongoDB. documentType={}, documentNumber={}, reason={}",
                            type, number, e.getMessage());
                    return true;
                });
    }

    private Completable safeCacheSave(Customer customer) {
        Completable op = cache.save(customer);

        if (op == null) {
            log.warn("[CUSTOMER-GET] Cache save returned null. customerId={}", customer.id());
            return Completable.complete();
        }

        return op.onErrorComplete(e -> {
            log.warn("[CUSTOMER-GET] Cache save failed. Continuing without cache. customerId={}, reason={}",
                    customer.id(), e.getMessage());
            return true;
        });
    }
}