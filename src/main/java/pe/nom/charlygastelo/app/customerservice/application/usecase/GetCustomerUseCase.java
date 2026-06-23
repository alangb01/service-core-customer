package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

@RequiredArgsConstructor
public class GetCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;

    public Maybe<Customer> byId(String id) {
        return customerRepository.findById(id);
    }

    public Maybe<Customer> byDocument(String type, String number) {
        return customerRepository.findByDocument(type, number);
    }
}