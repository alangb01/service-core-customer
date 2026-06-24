package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.exception.CustomerRepositoryException;

/**
 * Use case responsible for updating an existing customer.
 */
@RequiredArgsConstructor
@Slf4j
public class UpdateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;

    /**
     * Updates an existing customer by identifier.
     *
     * @param id       customer identifier
     * @param customer customer data to update
     * @return updated customer
     */
    public Maybe<Customer> execute(String id, Customer customer) {

        log.info("Starting update process for customer {}", id);

        return customerRepository.findById(id)
                .doOnSuccess(existing ->
                        log.debug("Customer {} found in MongoDB", id)
                )
                .doOnComplete(() ->
                        log.warn("Customer {} not found in MongoDB", id)
                )
                .switchIfEmpty(
                        Single.error(new CustomerNotFoundException("Customer not found: " + id))
                )

                // Actualizar campos
                .map(existing -> {
                    Customer updated = existing.updateWith(customer);
                    log.debug("Customer {} updated with new data", id);
                    return updated;
                })

                // Guardar en Mongo
                .flatMapMaybe(updated ->
                        customerRepository.save(updated)
                                .doOnSuccess(saved ->
                                        log.info("Customer {} updated successfully", id)
                                )
                                .doOnError(e ->
                                        log.error("Error saving updated customer {}: {}", id, e.getMessage(), e)
                                )
                                .toMaybe()
                )

                // Error técnico → envolver en excepción de infraestructura
                .onErrorResumeNext(e -> {
                    log.error("Technical error updating customer {}: {}", id, e.getMessage(), e);
                    return Maybe.error(new CustomerRepositoryException("Error updating customer", e));
                });
    }
}
