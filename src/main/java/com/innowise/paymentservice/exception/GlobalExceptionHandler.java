package com.innowise.paymentservice.exception;

import com.innowise.paymentservice.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(HttpServletRequest serverRequest,
                                                                     ServiceUnavailableException serviceUnavailableException) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.toString());
        errorResponse.setMessage(serviceUnavailableException.getMessage());
        errorResponse.setPath(serverRequest.getRequestURI());
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(HttpServletRequest serverRequest,
                                                                     ResourceNotFoundException resourceNotFoundException) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.NOT_FOUND.toString());
        errorResponse.setMessage(resourceNotFoundException.getMessage());
        errorResponse.setPath(serverRequest.getRequestURI());
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
