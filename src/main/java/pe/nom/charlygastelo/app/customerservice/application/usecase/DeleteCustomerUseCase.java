package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

/**
 * Use case responsible for deleting an existing customer.
 */
@Slf4j
@RequiredArgsConstructor
public class DeleteCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerCachePort cache;

    /**
     * Deletes an existing customer by identifier.
     *
     * @param id customer identifier
     * @return completed operation when the deletion is completed
     */
    public Completable execute(String id) {
        log.info("Request received to delete customer {}", id);
        return customerRepository.existsById(id)
                .doOnSuccess(exists -> log.info("Checking existence for customer {}", id))
                .flatMapCompletable(exists -> {
                    if (!exists) {
                        log.warn("Customer {} not found", id);
                        return Completable.error(new CustomerNotFoundException("Customer not found: " + id));
                    }

                    log.info("Deleting customer {}", id);
                    return customerRepository.deleteById(id)
                            .andThen(cache.delete(id))
                            .doOnComplete(() -> log.info("Customer {} deleted", id));
                });
    }



}