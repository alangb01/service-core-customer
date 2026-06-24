package pe.nom.charlygastelo.app.customerservice.infrastructure.events.exception;

public class CustomerEventProducerException extends RuntimeException {
    public CustomerEventProducerException(String message) {
        super(message);
    }
}
