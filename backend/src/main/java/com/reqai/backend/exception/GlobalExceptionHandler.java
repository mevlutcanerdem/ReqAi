package com.reqai.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // Log to console
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("GLOBAL ERROR: " + ex.getClass().getName() + " - " + ex.getMessage());
    }
}
