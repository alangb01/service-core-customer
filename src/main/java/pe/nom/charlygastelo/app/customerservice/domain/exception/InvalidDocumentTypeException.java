package pe.nom.charlygastelo.app.customerservice.domain.exception;

public class InvalidDocumentTypeException extends RuntimeException {
    public InvalidDocumentTypeException(String message) {
        super(message);
    }
}
