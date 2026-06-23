package pe.nom.charlygastelo.app.customerservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void shouldCreateCustomerRecord() {
        Customer customer = new Customer(
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

        assertThat(customer.id()).isEqualTo("1");
        assertThat(customer.documentType().toString()).isEqualTo("DNI");
        assertThat(customer.documentNumber()).isEqualTo("12345678");
        assertThat(customer.name()).isEqualTo("Juan");
        assertThat(customer.lastName()).isEqualTo("Perez");
        assertThat(customer.email()).isEqualTo("juan@test.com");
        assertThat(customer.phone()).isEqualTo("999999999");
        assertThat(customer.active()).isTrue();
    }
}