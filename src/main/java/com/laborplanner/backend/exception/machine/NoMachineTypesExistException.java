package com.laborplanner.backend.exception.machine;

public class NoMachineTypesExistException extends RuntimeException {
  public NoMachineTypesExistException() {
    super("No machine types exist. Cannot create machine.");
  }
}
