package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerAlreadyExistsException;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerServiceUnavailableException;
import pe.nom.charlygastelo.app.customerservice.domain.exception.CustomerProfileNotAllowedException;
import pe.nom.charlygastelo.app.customerservice.domain.exception.CustomerStateException;
import pe.nom.charlygastelo.app.customerservice.domain.exception.InvalidCustomerTypeException;
import pe.nom.charlygastelo.app.customerservice.domain.exception.InvalidDocumentTypeException;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.ErrorResponse;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.exception.CustomerRepositoryException;
import pe.nom.charlygastelo.app.customerservice.infrastructure.cache.exception.CustomerCacheException;
import pe.nom.charlygastelo.app.customerservice.infrastructure.events.exception.CustomerEventProducerException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // 409 - Ya existe
    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(CustomerAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CUSTOMER_ALREADY_EXISTS", ex.getMessage()));
    }

    // 404 - No encontrado
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CustomerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("CUSTOMER_NOT_FOUND", ex.getMessage()));
    }

    // 503 - Servicio externo caído
    @ExceptionHandler(CustomerServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable(CustomerServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("CUSTOMER_SERVICE_UNAVAILABLE", ex.getMessage()));
    }

    // 400 - Errores de dominio
    @ExceptionHandler({
            InvalidDocumentTypeException.class,
            InvalidCustomerTypeException.class,
            CustomerProfileNotAllowedException.class
    })
    public ResponseEntity<ErrorResponse> handleDomainErrors(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("DOMAIN_VALIDATION_ERROR", ex.getMessage()));
    }

    // 409 - Estado inválido del cliente
    @ExceptionHandler(CustomerStateException.class)
    public ResponseEntity<ErrorResponse> handleState(CustomerStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CUSTOMER_STATE_INVALID", ex.getMessage()));
    }

    // 500 - Errores técnicos
    @ExceptionHandler({
            CustomerRepositoryException.class,
            CustomerCacheException.class,
            CustomerEventProducerException.class,
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handleInfrastructureErrors(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
    }
}

