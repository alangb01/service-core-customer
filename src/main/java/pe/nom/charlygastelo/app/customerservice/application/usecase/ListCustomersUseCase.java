package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

@RequiredArgsConstructor
public class ListCustomersUseCase {

    private final CustomerRepositoryPort customerRepository;

    public Flowable<Customer> all() {
        return customerRepository.findAll();
    }
}