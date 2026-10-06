package com.shared.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseApplicationException {
    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}