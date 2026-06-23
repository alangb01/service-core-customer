package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerServicePort;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListCustomersUseCaseTest {

    private final CustomerServicePort service = Mockito.mock(CustomerServicePort.class);
    private final ListCustomersUseCase useCase = new ListCustomersUseCase(service);

    @Test
    void allShouldReturnCustomers() {
        Customer customer = customer();

        when(service.getAll()).thenReturn(Flowable.just(customer));

        StepVerifier.create(useCase.all())
                .expectNext(customer)
                .verifyComplete();

        verify(service).getAll();
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