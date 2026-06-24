package pe.nom.charlygastelo.app.customerservice.application.exception;

public class CustomerValidationException extends RuntimeException {
    public CustomerValidationException(String message) {
        super(message);
    }
}
