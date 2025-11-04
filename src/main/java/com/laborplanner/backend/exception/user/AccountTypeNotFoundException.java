package com.laborplanner.backend.exception.user;

public class AccountTypeNotFoundException extends RuntimeException {
  public AccountTypeNotFoundException(String uuid) {
    super("AccountType not found with UUID: " + uuid);
  }
}
