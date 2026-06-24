package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerAlreadyExistsException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;


@RequiredArgsConstructor
public class CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerEventPort producer;

    public Single<Customer> execute(Customer customer) {
        return customerRepository
                .checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
                        customer.customerType().toString(),
                        customer.documentType().toString(),
                        customer.documentNumber()
                ) // → Single<Boolean>

                .flatMap(exists -> {
                    if (exists) {
                        return Single.error(new CustomerAlreadyExistsException(customer.documentNumber()));
                    }
                    return customerRepository.save(customer); // → Single<Customer>
                })

                // Publicar evento dentro del flujo
                .flatMap(saved ->
                        producer.publishCustomerCreatedEvent(saved)
                                .andThen(Single.just(saved))
                );
    }
}