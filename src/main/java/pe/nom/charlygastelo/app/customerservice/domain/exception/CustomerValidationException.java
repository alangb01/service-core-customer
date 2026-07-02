package pe.nom.charlygastelo.app.customerservice.domain.exception;

public class CustomerValidationException extends RuntimeException {
    public CustomerValidationException(String message) {
        super(message);
    }
}
