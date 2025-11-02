package com.laborplanner.backend.exception.machine;

public class DuplicateMachineStatusNameException extends RuntimeException {
  public DuplicateMachineStatusNameException(String name) {
    super("Machine status name already exists: " + name);
  }
}
