package com.fitness.app.user_service.exceptions;

import org.springframework.stereotype.Component;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String string) {
        super(string);
    }
}

