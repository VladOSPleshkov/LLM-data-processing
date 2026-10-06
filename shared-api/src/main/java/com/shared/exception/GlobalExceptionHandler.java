package com.shared.exception;

import com.shared.dto.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseApplicationException.class)
    public ResponseEntity<ErrorResponse> handleBaseApplicationException(
            BaseApplicationException ex,
            HttpServletRequest request) {

        HttpStatus status = ex.getStatus();

        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(), // "Not Found", "Conflict" и тд
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Ошибки синтаксиса JSON (400)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Некорректный формат JSON запроса",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Недопустимый HTTP-метод (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                "HTTP метод " + ex.getMethod() + " не поддерживается для данного эндпоинта",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Catch-all: непредвиденные системные ошибки (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Произошла внутренняя ошибка сервера: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}