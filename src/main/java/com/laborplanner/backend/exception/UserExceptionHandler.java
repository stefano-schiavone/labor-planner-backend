package com.laborplanner.backend.exception;

import com.laborplanner.backend.dto.ApiError;
import com.laborplanner.backend.exception.user.UserNotFoundException;
import com.laborplanner.backend.exception.user.DuplicateUserEmailException;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class UserExceptionHandler {

   @ExceptionHandler(UserNotFoundException.class)
   public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
      ApiError error = ApiError.of(
            "User not found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
   }

   // Handle Spring Security login failures
   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
      ApiError error = ApiError.of(
            "Incorrect credentials",
            "Email or password is incorrect",
            HttpStatus.UNAUTHORIZED);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
   }

   @ExceptionHandler(DuplicateUserEmailException.class)
   public ResponseEntity<ApiError> handleDuplicateEmail(DuplicateUserEmailException ex) {
      ApiError error = ApiError.of(
            "Duplicate email",
            ex.getMessage(),
            HttpStatus.CONFLICT);
      return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
   }

   @ExceptionHandler(RuntimeException.class)
   public ResponseEntity<ApiError> handleGenericRuntime(RuntimeException ex) {
      ApiError error = ApiError.of(
            "Invalid credentials",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
   }
}
