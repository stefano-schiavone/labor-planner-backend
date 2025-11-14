package com.laborplanner.backend.exception;

import com.laborplanner.backend.dto.ApiError;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex) {
    // Log stack trace
    log.error("Uncaught exception occurred: ", ex);

    // API error response
    ApiError error =
        ApiError.of("Internal server error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
    // Extract validation errors
    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.toList());

    // Log the error
    log.warn("Validation failed: {}", errors);

    // Return structured API error response
    ApiError error =
        ApiError.of("Validation Failed", String.join("; ", errors), HttpStatus.BAD_REQUEST);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}
