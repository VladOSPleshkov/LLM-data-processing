package com.shared.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseApplicationException extends RuntimeException{
    private final HttpStatus status;
    protected BaseApplicationException(String message, HttpStatus status) {
        super(message); this.status = status; }
}
