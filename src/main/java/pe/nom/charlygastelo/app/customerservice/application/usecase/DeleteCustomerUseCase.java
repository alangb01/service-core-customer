package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

/**
 * Use case responsible for deleting an existing customer.
 */
@RequiredArgsConstructor
public class DeleteCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;

    /**
     * Deletes an existing customer by identifier.
     *
     * @param id customer identifier
     * @return completed operation when the deletion is completed
     */
    public Completable execute(String id) {
        return customerRepository.existsById(id)
                .flatMapCompletable(exists -> Boolean.TRUE.equals(exists)
                        ? customerRepository.deleteById(id)
                        : Completable.error(new CustomerNotFoundException("Customer not found: " + id)));
    }
}