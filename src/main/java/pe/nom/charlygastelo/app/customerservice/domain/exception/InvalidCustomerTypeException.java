package pe.nom.charlygastelo.app.customerservice.domain.exception;

public class InvalidCustomerTypeException extends RuntimeException {
    public InvalidCustomerTypeException(String message) {
        super(message);
    }
}
