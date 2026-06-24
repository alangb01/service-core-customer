package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListCustomersUseCaseTest {

    private final CustomerRepositoryPort repository = Mockito.mock(CustomerRepositoryPort.class);
    private final ListCustomersUseCase useCase = new ListCustomersUseCase(repository);

    @Test
    void allShouldReturnCustomers() {
        Customer customer = customer();

        when(repository.findAll()).thenReturn(Flowable.just(customer));

        TestSubscriber<Customer> test = useCase.all().test();

        test.assertValue(customer);
        test.assertComplete();
        test.assertNoErrors();


        verify(repository).findAll();
    }

    private Customer customer() {
        return new Customer(
                "1",
                CustomerType.PERSONAL,
                DocumentType.DNI,
                "DNI",
                "12345678",
                "Juan Perez",
                "juan@test.com",
                "999999999",
                true
        );
    }
}