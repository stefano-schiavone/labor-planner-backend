package com.laborplanner.backend.exception;

import com.laborplanner.backend.dto.ApiError;
import com.laborplanner.backend.exception.machine.DuplicateMachineNameException;
import com.laborplanner.backend.exception.machine.MachineNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MachineExceptionHandler {

  @ExceptionHandler(MachineNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(MachineNotFoundException ex) {
    ApiError error = ApiError.of("Machine not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(DuplicateMachineNameException.class)
  public ResponseEntity<ApiError> handleConflict(DuplicateMachineNameException ex) {
    ApiError error = ApiError.of("Duplicate machine name", ex.getMessage(), HttpStatus.CONFLICT);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  // TODO: Add Machine Status Exceptions Handling
}
