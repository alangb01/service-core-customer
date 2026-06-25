package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerAlreadyExistsException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.CustomerEventProducer;

import static org.mockito.Mockito.*;

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
                customer.documentNumber()
        )).thenReturn(Single.just(false));

        when(repository.save(customer))
                .thenReturn(Single.just(customer));

        when(producer.publishCustomerCreatedEvent(customer))
                .thenReturn(Completable.complete());

        TestObserver<Customer> observer = useCase.execute(customer).test();

        observer.assertComplete();
        observer.assertValue(customer);
        observer.assertNoErrors();

        verify(repository).save(customer);
        verify(producer).publishCustomerCreatedEvent(customer);
    }

    @Test
    void executeShouldReturnErrorWhenCustomerAlreadyExists() {
        Customer customer = customer();

        when(repository.checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
                customer.customerType().toString(),
                customer.documentType().toString(),
                customer.documentNumber()
        )).thenReturn(Single.just(true));

        TestObserver<Customer> observer = useCase.execute(customer).test();

        observer.assertError(CustomerAlreadyExistsException.class);

        verify(repository, never()).save(any());
        verify(producer, never()).publishCustomerCreatedEvent(any());
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