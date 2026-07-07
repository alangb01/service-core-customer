package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerResponseTest {

    @Test
    void shouldCreateResponseRecord() {
        CustomerResponse response = new CustomerResponse(
                "1",
                "PERSONAL",
                "DNI",
                "12345678",
                "REGULAR",
                "Juan",
                "Perez",
                "juan@test.com",
                "999999999",
                true
        );

        assertThat(response.id()).isEqualTo("1");
        assertThat(response.documentType()).isEqualTo("DNI");
        assertThat(response.documentNumber()).isEqualTo("12345678");
        assertThat(response.name()).isEqualTo("Juan");
        assertThat(response.lastName()).isEqualTo("Perez");
        assertThat(response.email()).isEqualTo("juan@test.com");
        assertThat(response.phone()).isEqualTo("999999999");
        assertThat(response.active()).isTrue();
    }
}