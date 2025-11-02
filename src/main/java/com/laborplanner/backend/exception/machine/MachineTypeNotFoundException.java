package com.laborplanner.backend.exception.machine;

public class MachineTypeNotFoundException extends RuntimeException {
  public MachineTypeNotFoundException(String uuid) {
    super("Machine type not found: " + uuid);
  }
}
