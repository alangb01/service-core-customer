package pe.nom.charlygastelo.app.customerservice.infrastructure.cache.exception;

public class CustomerCacheException extends RuntimeException {
    public CustomerCacheException(String message) {
        super(message);
    }

    public CustomerCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
