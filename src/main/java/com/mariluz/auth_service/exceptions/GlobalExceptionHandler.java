package com.mariluz.auth_service.exceptions;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handler MethodNotSupported Exception
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(
        HttpRequestMethodNotSupportedException ex
    ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            Map.of("error", ex.getMessage())
        );
    }

    // Handler Email In Use Exception
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyInUse(
        EmailAlreadyInUseException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of("error", ex.getMessage())
        );
    }

    // Handler Data Integrity Violation Exception (ej: email duplicado por race condition)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
        DataIntegrityViolationException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of("error", "El correo ya esta registrado.")
        );
    }

    // Handler Invalid Credentials Exception
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(
        InvalidCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of("error", ex.getMessage())
        );
    }

    // Handler Email Not Found Exception
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmailNotFound(
        EmailNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of("error", ex.getMessage())
        );
    }

    // Handler Jakarta contraint violation exception
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleJakartaViolationException(
        ConstraintViolationException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(v -> {
            errors.put(v.getPropertyPath().toString(), v.getMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Global Handler | Validacion BindingResult ahora se corrobora aqui en el global handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
            .getFieldErrors()
            .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Handler generico de ultima linea para excepciones no contempladas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(
        Exception ex
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of("error", "Error interno del servidor.")
        );
    }
}
