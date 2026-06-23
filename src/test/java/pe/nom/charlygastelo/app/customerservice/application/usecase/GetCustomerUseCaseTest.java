package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerServicePort;
import io.reactivex.rxjava3.core.Single;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCustomerUseCaseTest {

    private final CustomerServicePort service = Mockito.mock(CustomerServicePort.class);
    private final GetCustomerUseCase useCase = new GetCustomerUseCase(service);

    @Test
    void byIdShouldReturnCustomer() {
        Customer customer = customer();

        when(service.getById("1")).thenReturn(Maybe.just(customer));

        StepVerifier.create(useCase.byId("1").toFlowable())
                .expectNext(customer)
                .verifyComplete();

        verify(service).getById("1");
    }


    @Test
    void byDocumentShouldReturnCustomer() {
        Customer customer = customer();

        when(service.getByDocument("DNI", "12345678")).thenReturn(Maybe.just(customer));

        StepVerifier.create(useCase.byDocument("DNI", "12345678").toFlowable())
                .expectNext(customer)
                .verifyComplete();

        verify(service).getByDocument("DNI", "12345678");
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