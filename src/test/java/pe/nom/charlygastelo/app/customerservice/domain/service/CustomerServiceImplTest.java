package pe.nom.charlygastelo.app.customerservice.domain.service;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceImplTest {

    private final CustomerRepositoryPort repository = Mockito.mock(CustomerRepositoryPort.class);
    private final CustomerServiceImpl service = new CustomerServiceImpl(repository);

    @Test
    void createShouldSaveCustomer() {
        Customer customer = customer();

        when(repository.save(customer)).thenReturn(Single.just(customer));

        StepVerifier.create(service.create(customer).toFlowable())
                .expectNext(customer)
                .verifyComplete();

        verify(repository).save(customer);
    }

    @Test
    void getByIdShouldFindCustomerById() {
        Customer customer = customer();
        when(repository.findById("1")).thenReturn(Maybe.just(customer));

        StepVerifier.create(service.getById("1").toFlowable())
                .expectNext(customer)
                .verifyComplete();

        verify(repository).findById("1");
    }

    @Test
    void getByDocumentShouldFindCustomerByDocument() {
        Customer customer = customer();

        when(repository.findByDocument("DNI", "12345678"))
                .thenReturn(Maybe.just(customer));

        StepVerifier.create(service.getByDocument("DNI", "12345678").toFlowable())
                .expectNext(customer)
                .verifyComplete();

        verify(repository).findByDocument("DNI", "12345678");
    }

    @Test
    void getAllShouldFindAllCustomers() {
        Customer customer = customer();

        when(repository.findAll()).thenReturn(Flowable.just(customer));

        StepVerifier.create(service.getAll())
                .expectNext(customer)
                .verifyComplete();

        verify(repository).findAll();
    }

    private Customer customer() {
        return new Customer(
                "1",
                CustomerType.PERSONAL,
                DocumentType.DNI,
                "12345678",
                "Juan",
                "Perez",
                "juan@test.com",
                "999999999",
                true
        );
    }
}