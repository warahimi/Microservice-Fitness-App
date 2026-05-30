package com.fitness.app.user_service.exceptions;

import org.springframework.stereotype.Component;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
