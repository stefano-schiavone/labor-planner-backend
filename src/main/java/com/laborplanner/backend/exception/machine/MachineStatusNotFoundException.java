package com.laborplanner.backend.exception.machine;

public class MachineStatusNotFoundException extends RuntimeException {
  public MachineStatusNotFoundException(String uuid) {
    super("Machine status not found: " + uuid);
  }
}
