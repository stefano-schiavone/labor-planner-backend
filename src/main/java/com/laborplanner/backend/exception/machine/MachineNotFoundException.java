package com.laborplanner.backend.exception.machine;

public class MachineNotFoundException extends RuntimeException {
  public MachineNotFoundException(String uuid) {
    super("Machine not found: " + uuid);
  }
}
