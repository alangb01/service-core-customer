package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerAlreadyExistsException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.CustomerEventProducer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateCustomerUseCaseTest {

    private final CustomerRepositoryPort repository = Mockito.mock(CustomerRepositoryPort.class);
    private final CustomerEventProducer producer = Mockito.mock(CustomerEventProducer.class);
    private final CreateCustomerUseCase useCase = new CreateCustomerUseCase(repository,producer);

    @Test
    void executeShouldCreateCustomerWhenCustomerDoesNotExist() {
        Customer customer = customer();

        when(repository.checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
                customer.customerType().toString(),
                customer.documentType().toString(),
                customer.documentNumber()))
                .thenReturn(Single.just(false));
        when(repository.save(customer))
                .thenReturn(Single.just(customer));

        useCase.execute(customer)
                .test()
                .assertValue(customer)
                .assertComplete()
                .assertNoErrors();

        verify(repository).checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
                customer.customerType().toString(),
                customer.documentType().toString(),
                customer.documentNumber()
            );
        verify(repository).save(customer);
    }

    @Test
    void executeShouldReturnErrorWhenCustomerAlreadyExists() {
        Customer customer = customer();

        when(repository.checkByCustomerTypeAndDocumentTypeAndDocumentNumber(customer.customerType().toString(),
                customer.documentType().toString(),
                customer.documentNumber()
        ))
                .thenReturn(Single.just(true));

        useCase.execute(customer)
                .test()
                .assertError(CustomerAlreadyExistsException.class)
                .assertNotComplete();

        verify(repository).checkByCustomerTypeAndDocumentTypeAndDocumentNumber(customer.customerType().toString(),
                customer.documentType().toString(),
                customer.documentNumber()
        );
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