package pe.nom.charlygastelo.app.customerservice.domain.exception;

public class CustomerExistsException extends RuntimeException {
    public CustomerExistsException(String message) {
        super(message);
    }
}
