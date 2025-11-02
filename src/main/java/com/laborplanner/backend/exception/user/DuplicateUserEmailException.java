package com.laborplanner.backend.exception.user;

public class DuplicateUserEmailException extends RuntimeException {
  public DuplicateUserEmailException(String email) {
    super("User with email '" + email + "' already exists.");
  }
}
