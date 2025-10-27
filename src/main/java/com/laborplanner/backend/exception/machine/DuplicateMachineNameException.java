package com.laborplanner.backend.exception.machine;

public class DuplicateMachineNameException extends RuntimeException {
  public DuplicateMachineNameException(String name) {
    super("A machine with the name '" + name + "' already exists.");
  }
}
