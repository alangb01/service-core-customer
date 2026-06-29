package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class DeleteCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerCachePort cache;
    private final CustomerEventProducerPort producer;

    public Completable execute(String id) {
        log.info("[CUSTOMER-DELETE] Request received to delete customer {}", id);

        return customerRepository.existsById(id)
                .flatMapCompletable(exists -> {
                    if (!exists) {
                        return Completable.error(
                                new CustomerNotFoundException("Customer not found: " + id)
                        );
                    }

                    return customerRepository.deleteById(id)
                            .andThen(safeCacheDelete(id))
                            .andThen(producer.publishCustomerDeleted(id));
                })
                .doOnComplete(() ->
                        log.info("[CUSTOMER-DELETE] Customer deletion completed. id={}", id))
                .doOnError(e ->
                        log.error("[CUSTOMER-DELETE] Error deleting customer. id={}, reason={}",
                                id, e.getMessage(), e));
    }

    private Completable safeCacheDelete(String id) {
        Completable op = cache.delete(id);

        if (op == null) {
            log.warn("[CUSTOMER-DELETE] Cache delete returned null. id={}", id);
            return Completable.complete();
        }

        return op.onErrorComplete(e -> {
            log.warn("[CUSTOMER-DELETE] Cache delete failed. Continuing. id={}, reason={}",
                    id, e.getMessage());
            return true;
        });
    }
}