package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCustomerUseCaseTest {

    private final CustomerRepositoryPort repository = Mockito.mock(CustomerRepositoryPort.class);
    private final CustomerCachePort cache = Mockito.mock(CustomerCachePort.class);
    private final GetCustomerUseCase useCase = new GetCustomerUseCase(repository, cache);

    @Test
    void byIdShouldReturnCustomer() {
        Customer customer = customer();

        when(repository.findById("1")).thenReturn(Maybe.just(customer));

        useCase.byId("1")
                .test()
                .assertValue(customer)
                .assertComplete()
                .assertNoErrors();

        verify(repository).findById("1");
    }



    @Test
    void byDocumentShouldReturnCustomer() {
        Customer customer = customer();

        when(repository.findByDocument("DNI", "12345678")).thenReturn(Maybe.just(customer));

        useCase.byDocument("DNI", "12345678")
                .test()
                .assertValue(customer)
                .assertComplete()
                .assertNoErrors();

        verify(repository).findByDocument("DNI", "12345678");
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