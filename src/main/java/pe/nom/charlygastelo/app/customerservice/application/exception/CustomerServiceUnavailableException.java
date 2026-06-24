package pe.nom.charlygastelo.app.customerservice.application.exception;

public class CustomerServiceUnavailableException extends RuntimeException {
    public CustomerServiceUnavailableException(String message) {
        super(message);
    }
}
