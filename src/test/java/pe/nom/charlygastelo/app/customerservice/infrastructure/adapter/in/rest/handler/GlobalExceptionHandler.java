package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -----------------------------
    // 500 - INTERNAL SERVER ERROR (GENÉRICO)
    // -----------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
    }
}
