package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.exception;

public class CustomerRepositoryException extends RuntimeException {
    public CustomerRepositoryException(String message) {
        super(message);
    }

    public CustomerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
