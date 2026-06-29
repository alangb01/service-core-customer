package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.exception.CustomerRepositoryException;

@RequiredArgsConstructor
@Slf4j
public class UpdateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerCachePort cache;
    private final CustomerEventProducerPort producer;

    public Maybe<Customer> execute(String id, Customer customer) {
        log.info("[CUSTOMER-UPDATE] Starting update process. id={}", id);

        return customerRepository.findById(id)
                .switchIfEmpty(Maybe.error(
                        new CustomerNotFoundException("Customer not found: " + id)
                ))
                .map(existing -> existing.updateWith(customer))
                .flatMap(updated ->
                        customerRepository.save(updated).toMaybe()
                )
                .flatMap(saved ->
                        safeCacheRefresh(id, saved)
                                .andThen(Maybe.just(saved))
                )
                .flatMap(saved ->
                        producer.publishCustomerUpdated(saved)
                                .andThen(Maybe.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("[CUSTOMER-UPDATE] Customer updated successfully. id={}", id))
                .onErrorResumeNext(e -> {
                    if (e instanceof CustomerNotFoundException) {
                        return Maybe.error(e);
                    }

                    log.error("[CUSTOMER-UPDATE] Technical error updating customer. id={}, reason={}",
                            id, e.getMessage(), e);

                    return Maybe.error(new CustomerRepositoryException("Error updating customer", e));
                });
    }

    private Completable safeCacheRefresh(String id, Customer customer) {
        return safeCacheDelete(id)
                .andThen(safeCacheSave(customer));
    }

    private Completable safeCacheDelete(String id) {
        Completable op = cache.delete(id);

        if (op == null) {
            log.warn("[CUSTOMER-UPDATE] Cache delete returned null. id={}", id);
            return Completable.complete();
        }

        return op.onErrorComplete(e -> {
            log.warn("[CUSTOMER-UPDATE] Cache delete failed. Continuing. id={}, reason={}",
                    id, e.getMessage());
            return true;
        });
    }

    private Completable safeCacheSave(Customer customer) {
        Completable op = cache.save(customer);

        if (op == null) {
            log.warn("[CUSTOMER-UPDATE] Cache save returned null. id={}", customer.id());
            return Completable.complete();
        }

        return op.onErrorComplete(e -> {
            log.warn("[CUSTOMER-UPDATE] Cache save failed. Continuing. id={}, reason={}",
                    customer.id(), e.getMessage());
            return true;
        });
    }
}