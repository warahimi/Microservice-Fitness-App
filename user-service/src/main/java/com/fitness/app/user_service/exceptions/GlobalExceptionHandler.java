package com.fitness.app.user_service.exceptions;

import com.fitness.app.user_service.model.UserRole;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message("Validation failed")
                .errorType("ValidationException")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorMessage> handleUserExists(
            UserAlreadyExistException ex,
            WebRequest request) {

        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .errorType("UserAlreadyExistException")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(null)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {

        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .errorType("ResourceNotFoundException")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleUnreadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        String message = "Malformed JSON request";
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum()) {
                message = "Invalid value for enum. Accepted values: "
                        + Arrays.toString(UserRole.values());
            }
        }

        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .errorType("HttpMessageNotReadableException")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(null)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneric(
            Exception ex,
            WebRequest request) {

        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .message("Something went wrong. Please try again later.")
                .errorType("InternalServerError")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(null)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    // exception handler for duplicate DB errors related to unique constraints on username and email
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {

        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .message("Username or email already exists")
                .errorType("DataIntegrityViolationException")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(null)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}