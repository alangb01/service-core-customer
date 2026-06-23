package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pe.nom.charlygastelo.app.customerservice.domain.exception.CustomerExistsException;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerExistsException.class)
    public ResponseEntity<ErrorResponse> handleDocumentAlreadyExists(CustomerExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("DOCUMENT_ALREADY_EXISTS", ex.getMessage()));
    }
}
