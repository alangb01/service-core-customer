package pe.nom.charlygastelo.app.customerservice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerServiceApplicationTests {

    @Test
    void shouldCreateApplicationInstance() {
        CustomerServiceApplication application = new CustomerServiceApplication();

        assertThat(application).isNotNull();
    }
}