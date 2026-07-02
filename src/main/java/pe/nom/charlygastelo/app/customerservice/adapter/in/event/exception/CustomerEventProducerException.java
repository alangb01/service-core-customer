package pe.nom.charlygastelo.app.customerservice.adapter.in.event.exception;

public class CustomerEventProducerException extends RuntimeException {
    public CustomerEventProducerException(String message) {
        super(message);
    }
}
