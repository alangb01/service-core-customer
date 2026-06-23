package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

/**
 * Use case responsible for updating an existing customer.
 */
@RequiredArgsConstructor
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

        return customerRepository.findById(id)
                .switchIfEmpty(Single.error(new CustomerNotFoundException("Customer not found: " + id)))
                .map(existing -> existing.updateWith(customer))
                .flatMapMaybe(updated -> customerRepository.save(updated).toMaybe());
    }
}
