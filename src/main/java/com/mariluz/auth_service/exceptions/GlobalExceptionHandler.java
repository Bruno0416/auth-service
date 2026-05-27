package com.mariluz.auth_service.exceptions;

import com.mariluz.auth_service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
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
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message(ex.getMessage())
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler Email In Use Exception
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUse(
        EmailAlreadyInUseException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler Data Integrity Violation Exception (ej: email duplicado por race condition)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
        DataIntegrityViolationException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message("El correo ya esta registrado")
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler Invalid Credentials Exception
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
        InvalidCredentialsException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler Jakarta contraint violation exception
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleJakartaViolationException(
        ConstraintViolationException ex,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(v -> {
            errors.put(v.getPropertyPath().toString(), v.getMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de validacion")
                .errors(errors)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Global Handler | Validacion BindingResult ahora se corrobora aqui en el global handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
            .getFieldErrors()
            .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de validacion")
                .errors(errors)
                .endpoint(request.getRequestURI())
                .build()
        );
    }

    // Handler generico de ultima linea para excepciones no contempladas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Error interno del servidor")
                .endpoint(request.getRequestURI())
                .build()
        );
    }
}
