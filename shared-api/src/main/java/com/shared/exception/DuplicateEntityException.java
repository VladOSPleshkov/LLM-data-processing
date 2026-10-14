package com.shared.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEntityException extends BaseApplicationException {
    public DuplicateEntityException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
