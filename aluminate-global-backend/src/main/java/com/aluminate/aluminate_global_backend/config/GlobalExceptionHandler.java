package com.aluminate.aluminate_global_backend.config;


import com.aluminate.aluminate_global_backend.config.exception.DuplicateEmailException;
import com.aluminate.aluminate_global_backend.config.exception.DuplicateOrganizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handle custom exceptions (e.g., AlreadyExistsException)



    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseWrapper<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(DuplicateOrganizationException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDuplicateOrg(DuplicateOrganizationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseWrapper<>(false, ex.getMessage(), null));
    }

    // Keep generic handler as fallback
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseWrapper<>(false, ex.getMessage(), null));
    }

    // Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                new ResponseWrapper<>(false, errorMessages, null),
                HttpStatus.BAD_REQUEST
        );
    }

    // Catch-all for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                new ResponseWrapper<>(false, "Internal server error", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
