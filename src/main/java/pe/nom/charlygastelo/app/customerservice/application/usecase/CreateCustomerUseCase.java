package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.exception.CustomerExistsException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;


@RequiredArgsConstructor
public class CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerEventPort producer;

    public Maybe<Customer> execute(Customer customer) {
        return customerRepository.checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
                        customer.customerType().toString(),
                        customer.documentType().toString(),
                        customer.documentNumber()) // → Single<Boolean>
                .flatMapMaybe(exists -> {
                    if (exists) {
                        return Maybe.error(new CustomerExistsException(customer.documentNumber()));
                    }
                    return customerRepository.save(customer).toMaybe(); // → Single<Customer> → Maybe<Customer>
                }).doOnSuccess(producer::publishCustomerCreatedEvent);
    }


}