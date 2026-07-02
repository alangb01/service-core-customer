package pe.nom.charlygastelo.app.customerservice.domain.exception;

public class CustomerHasOverdueDebtException extends RuntimeException {
    public CustomerHasOverdueDebtException(String message) {
        super(message);
    }
}
