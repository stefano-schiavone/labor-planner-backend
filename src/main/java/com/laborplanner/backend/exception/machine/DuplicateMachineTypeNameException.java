package com.laborplanner.backend.exception.machine;

public class DuplicateMachineTypeNameException extends RuntimeException {
  public DuplicateMachineTypeNameException(String name) {
    super("Machine type name already exists: " + name);
  }
}
