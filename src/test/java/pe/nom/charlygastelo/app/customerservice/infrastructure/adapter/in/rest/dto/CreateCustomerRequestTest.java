package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCustomerRequestTest {

    @Test
    void shouldCreateRequestRecord() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "PERSONAL",
                "DNI",
                "12345678",
                "Juan",
                "Perez",
                "juan@test.com",
                "999999999",
                true
        );

        assertThat(request.documentType()).isEqualTo("DNI");
        assertThat(request.documentNumber()).isEqualTo("12345678");
        assertThat(request.name()).isEqualTo("Juan");
        assertThat(request.lastName()).isEqualTo("Perez");
        assertThat(request.email()).isEqualTo("juan@test.com");
        assertThat(request.phone()).isEqualTo("999999999");
    }
}