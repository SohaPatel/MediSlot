package com.medislot.backend.controller;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,String>> invalidRequest(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream().findFirst()
                .map(error -> error.getDefaultMessage()).orElse("Request validation failed.");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String,String>> dataConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "A record conflicts with existing data."));
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String,String>> statusException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("error", exception.getReason() == null ? "Request failed." : exception.getReason()));
    }
}
