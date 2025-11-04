package com.laborplanner.backend.exception.user;

public class DuplicateAccountTypeNameException extends RuntimeException {
  public DuplicateAccountTypeNameException(String name) {
    super("AccountType with name '" + name + "' already exists.");
  }
}
