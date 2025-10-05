package com.dao.cookbook.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handlingRuntimeException(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
