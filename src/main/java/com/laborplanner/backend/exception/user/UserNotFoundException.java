package com.laborplanner.backend.exception.user;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String uuid) {
    super("User not found with UUID: " + uuid);
  }
}
