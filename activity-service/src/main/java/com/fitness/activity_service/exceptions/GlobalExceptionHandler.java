package com.fitness.activity_service.exceptions;

import com.fitness.activity_service.model.ActivityType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleUnreadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        String message = "Malformed JSON request";
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum()) {
                message = "Invalid value for enum. Accepted values: "
                        + Arrays.toString(ActivityType.values());
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

    @ExceptionHandler(ActivityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(
            ActivityNotFoundException ex,
            WebRequest request) {

        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .errorType("ActivityNotFoundException")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
