package pe.nom.charlygastelo.app.customerservice.application.exception;

public class CustomerHasOverdueDebtException extends RuntimeException {
    public CustomerHasOverdueDebtException(String message) {
        super(message);
    }
}
