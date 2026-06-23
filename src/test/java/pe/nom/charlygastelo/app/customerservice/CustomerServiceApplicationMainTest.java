package pe.nom.charlygastelo.app.customerservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.eq;

class CustomerServiceApplicationMainTest {

    @Test
    void mainShouldRunSpringApplication() {
        String[] args = {"--spring.main.web-application-type=none"};

        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            CustomerServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(eq(CustomerServiceApplication.class), eq(args)));
        }
    }
}