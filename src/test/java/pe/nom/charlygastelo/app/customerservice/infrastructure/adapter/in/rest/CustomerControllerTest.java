package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import pe.nom.charlygastelo.app.customerservice.application.usecase.CreateCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.DeleteCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.GetCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.ListCustomersUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.UpdateCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CreateCustomerRequest;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CustomerResponse;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.mapper.RestMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    private final CreateCustomerUseCase createCustomerUseCase = Mockito.mock(CreateCustomerUseCase.class);
    private final GetCustomerUseCase getCustomerUseCase = Mockito.mock(GetCustomerUseCase.class);
    private final ListCustomersUseCase listCustomersUseCase = Mockito.mock(ListCustomersUseCase.class);
    private final UpdateCustomerUseCase updateCustomerUseCase = Mockito.mock(UpdateCustomerUseCase.class);
    private final DeleteCustomerUseCase deleteCustomerUseCase = Mockito.mock(DeleteCustomerUseCase.class);
    private final RestMapper restMapper = Mockito.mock(RestMapper.class);

    private final CustomerController controller = new CustomerController(
            createCustomerUseCase,
            getCustomerUseCase,
            listCustomersUseCase,
            updateCustomerUseCase,
            deleteCustomerUseCase,
            restMapper
    );

    @Test
    void createShouldReturnCustomerResponse() {
        CreateCustomerRequest request = request();
        Customer customerToCreate = customerWithoutId();
        Customer createdCustomer = customer();
        CustomerResponse response = response();

        when(restMapper.toDomain(request)).thenReturn(customerToCreate);
        when(createCustomerUseCase.execute(any(Customer.class))).thenReturn(Maybe.just(createdCustomer));
        when(restMapper.toResponse(createdCustomer)).thenReturn(response);

        controller.create(request)
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(result.getBody()).isEqualTo(response);
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(restMapper).toDomain(request);
        verify(createCustomerUseCase).execute(any(Customer.class));
        verify(restMapper).toResponse(createdCustomer);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(createCustomerUseCase).execute(captor.capture());

        Customer captured = captor.getValue();
        assertThat(captured.id()).isNull();
        assertThat(captured.customerType()).isEqualTo(CustomerType.PERSONAL);
        assertThat(captured.documentType()).isEqualTo(DocumentType.DNI);
        assertThat(captured.documentNumber()).isEqualTo("12345678");
        assertThat(captured.name()).isEqualTo("Juan");
        assertThat(captured.lastName()).isEqualTo("Perez");
        assertThat(captured.email()).isEqualTo("juan@test.com");
        assertThat(captured.phone()).isEqualTo("999999999");
        assertThat(captured.active()).isTrue();
    }

    @Test
    void getByIdShouldReturnOkWhenCustomerExists() {
        Customer customer = customer();
        CustomerResponse response = response();

        when(getCustomerUseCase.byId("1")).thenReturn(Maybe.just(customer));
        when(restMapper.toResponse(customer)).thenReturn(response);

        controller.getById("1")
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(result.getBody()).isEqualTo(response);
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(getCustomerUseCase).byId("1");
        verify(restMapper).toResponse(customer);
    }

    @Test
    void getByIdShouldReturnNotFoundWhenCustomerDoesNotExist() {
        when(getCustomerUseCase.byId("missing")).thenReturn(Maybe.empty());

        controller.getById("missing")
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(result.getBody()).isNull();
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(getCustomerUseCase).byId("missing");
    }

    @Test
    void getByDocumentShouldReturnOkWhenCustomerExists() {
        Customer customer = customer();
        CustomerResponse response = response();

        when(getCustomerUseCase.byDocument("DNI", "12345678")).thenReturn(Maybe.just(customer));
        when(restMapper.toResponse(customer)).thenReturn(response);

        controller.getByDocument("DNI", "12345678")
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(result.getBody()).isEqualTo(response);
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(getCustomerUseCase).byDocument("DNI", "12345678");
        verify(restMapper).toResponse(customer);
    }

    @Test
    void getByDocumentShouldReturnNotFoundWhenCustomerDoesNotExist() {
        when(getCustomerUseCase.byDocument("DNI", "00000000")).thenReturn(Maybe.empty());

        controller.getByDocument("DNI", "00000000")
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(result.getBody()).isNull();
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(getCustomerUseCase).byDocument("DNI", "00000000");
    }

    @Test
    void listShouldReturnCustomerResponses() {
        Customer customer = customer();
        CustomerResponse response = response();

        when(listCustomersUseCase.all()).thenReturn(Flowable.just(customer));
        when(restMapper.toResponse(customer)).thenReturn(response);

        controller.list()
                .test()
                .assertValue(response)
                .assertComplete()
                .assertNoErrors();

        verify(listCustomersUseCase).all();
        verify(restMapper).toResponse(customer);
    }

    @Test
    void updateShouldReturnOkWhenCustomerExists() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "PERSONAL",
                "DNI",
                "12345678",
                "Juan Updated",
                "Perez",
                "juan.updated@test.com",
                "988888888",
                true
        );

        Customer customerToUpdate = new Customer(
                null,
                CustomerType.PERSONAL,
                DocumentType.DNI,
                "12345678",
                "Juan Updated",
                "Perez",
                "juan.updated@test.com",
                "988888888",
                true
        );

        Customer updatedCustomer = new Customer(
                "1",
                CustomerType.PERSONAL,
                DocumentType.DNI,
                "12345678",
                "Juan Updated",
                "Perez",
                "juan.updated@test.com",
                "988888888",
                true
        );

        CustomerResponse updatedResponse = new CustomerResponse(
                "1",
                "PERSONAL",
                "DNI",
                "12345678",
                "Juan Updated",
                "Perez",
                "juan.updated@test.com",
                "988888888",
                true
        );

        when(restMapper.toDomain(request)).thenReturn(customerToUpdate);
        when(updateCustomerUseCase.execute(eq("1"), any(Customer.class))).thenReturn(Maybe.just(updatedCustomer));
        when(restMapper.toResponse(updatedCustomer)).thenReturn(updatedResponse);

        controller.update("1", request)
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(result.getBody()).isEqualTo(updatedResponse);
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(restMapper).toDomain(request);
        verify(updateCustomerUseCase).execute(eq("1"), any(Customer.class));
        verify(restMapper).toResponse(updatedCustomer);
    }

    @Test
    void updateShouldReturnNotFoundWhenCustomerDoesNotExist() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "PERSONAL",
                "DNI",
                "00000000",
                "Missing",
                "Customer",
                "missing@test.com",
                "900000000",
                true
        );

        Customer customerToUpdate = new Customer(
                null,
                CustomerType.PERSONAL,
                DocumentType.DNI,
                "00000000",
                "Missing",
                "Customer",
                "missing@test.com",
                "900000000",
                true
        );

        when(restMapper.toDomain(request)).thenReturn(customerToUpdate);
        when(updateCustomerUseCase.execute(eq("missing"), any(Customer.class))).thenReturn(Maybe.empty());

        controller.update("missing", request)
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(result.getBody()).isNull();
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(restMapper).toDomain(request);
        verify(updateCustomerUseCase).execute(eq("missing"), any(Customer.class));
    }

    @Test
    void deleteShouldReturnNoContentWhenCustomerExists() {
        when(getCustomerUseCase.byId("1")).thenReturn(Maybe.just(customer()));
        when(deleteCustomerUseCase.execute("1")).thenReturn(Completable.complete());

        controller.delete("1")
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
                    assertThat(result.getBody()).isNull();
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(getCustomerUseCase).byId("1");
        verify(deleteCustomerUseCase).execute("1");
    }

    @Test
    void deleteShouldReturnNotFoundWhenCustomerDoesNotExist() {
        when(getCustomerUseCase.byId("missing")).thenReturn(Maybe.empty());

        controller.delete("missing")
                .test()
                .assertValue(result -> {
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(result.getBody()).isNull();
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        verify(getCustomerUseCase).byId("missing");
        verify(deleteCustomerUseCase, never()).execute(anyString());
    }

    private CreateCustomerRequest request() {
        return new CreateCustomerRequest(
                "PERSONAL",
                "DNI",
                "12345678",
                "Juan",
                "Perez",
                "juan@test.com",
                "999999999",
                true
        );
    }

    private Customer customerWithoutId() {
        return new Customer(
                null,
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

    private CustomerResponse response() {
        return new CustomerResponse(
                "1",
                "PERSONAL",
                "DNI",
                "12345678",
                "Juan",
                "Perez",
                "juan@test.com",
                "999999999",
                true
        );
    }
}