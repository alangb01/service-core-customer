package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.model.ProfileType;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerRepositoryAdapterTest {

    private final ReactiveCustomerRepository repository = Mockito.mock(ReactiveCustomerRepository.class);
    private final PersistenceMapper mapper = new PersistenceMapper();
    private final CustomerRepositoryAdapter adapter = new CustomerRepositoryAdapter(repository, mapper);

    @Test
    void saveShouldMapDomainToDocumentAndReturnDomain() {
        Customer customer = customer();
        CustomerDocument savedDocument = document();

        when(repository.save(any(CustomerDocument.class)))
                .thenReturn(Mono.just(savedDocument));

        adapter.save(customer)
                .test()
                .assertValue(result -> {
                    assertThat(result.id()).isEqualTo("1");
                    assertThat(result.customerType()).isEqualTo(CustomerType.PERSONAL);
                    assertThat(result.documentType()).isEqualTo(DocumentType.DNI);
                    assertThat(result.documentNumber()).isEqualTo("12345678");
                    assertThat(result.name()).isEqualTo("Juan");
                    assertThat(result.lastName()).isEqualTo("Perez");
                    assertThat(result.email()).isEqualTo("juan@test.com");
                    assertThat(result.phone()).isEqualTo("999999999");
                    assertThat(result.active()).isTrue();
                    return true;
                })
                .assertComplete()
                .assertNoErrors();

        ArgumentCaptor<CustomerDocument> captor = ArgumentCaptor.forClass(CustomerDocument.class);
        verify(repository).save(captor.capture());

        CustomerDocument captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo("1");
        assertThat(captured.getCustomerType()).isEqualTo(CustomerType.PERSONAL);
        assertThat(captured.getDocumentType()).isEqualTo(DocumentType.DNI);
        assertThat(captured.getDocumentNumber()).isEqualTo("12345678");
        assertThat(captured.getName()).isEqualTo("Juan");
        assertThat(captured.getLastName()).isEqualTo("Perez");
        assertThat(captured.getEmail()).isEqualTo("juan@test.com");
        assertThat(captured.getPhone()).isEqualTo("999999999");
        assertThat(captured.isActive()).isTrue();
    }

    @Test
    void findByIdShouldReturnMappedDomain() {
        CustomerDocument document = document();

        when(repository.findById("1")).thenReturn(Mono.just(document));

        adapter.findById("1")
                .test()
                .assertValue(customer())
                .assertComplete()
                .assertNoErrors();

        verify(repository).findById("1");
    }


    @Test
    void findByDocumentShouldReturnMappedDomain() {
        CustomerDocument document = document();

        when(repository.findByDocumentTypeAndDocumentNumber("DNI", "12345678"))
                .thenReturn(Mono.just(document));

        adapter.findByDocument("DNI", "12345678")
                .test()
                .assertValue(customer())
                .assertComplete()
                .assertNoErrors();

        verify(repository).findByDocumentTypeAndDocumentNumber("DNI", "12345678");
    }


    @Test
    void findAllShouldReturnMappedDomains() {
        CustomerDocument document = document();

        when(repository.findAll()).thenReturn(Flux.just(document));

        adapter.findAll()
                .test()
                .assertValue(customer())
                .assertComplete()
                .assertNoErrors();

        verify(repository).findAll();
    }


    private Customer customer() {
        return new Customer(
                "1",
                CustomerType.PERSONAL,
                DocumentType.DNI,
                "12345678",
                ProfileType.REGULAR,
                "Juan",
                "Perez",
                "juan@test.com",
                "999999999",
                true
        );
    }

    private CustomerDocument document() {
        CustomerDocument document = new CustomerDocument();
        document.setId("1");
        document.setCustomerType(CustomerType.PERSONAL);
        document.setDocumentType(DocumentType.DNI);
        document.setDocumentNumber("12345678");
        document.setName("Juan");
        document.setLastName("Perez");
        document.setEmail("juan@test.com");
        document.setPhone("999999999");
        document.setActive(true);
        return document;
    }
}