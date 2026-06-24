package pe.nom.charlygastelo.app.customerservice.domain.exception;

public class CustomerProfileNotAllowedException extends RuntimeException {
    public CustomerProfileNotAllowedException(String message) {
        super(message);
    }
}
