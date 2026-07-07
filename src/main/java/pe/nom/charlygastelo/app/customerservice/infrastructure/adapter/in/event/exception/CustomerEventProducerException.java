package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.event.exception;

public class CustomerEventProducerException extends RuntimeException {
    public CustomerEventProducerException(String message) {
        super(message);
    }
}
