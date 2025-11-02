package com.laborplanner.backend.model;

import lombok.*;

@Getter
@Setter
public class MachineType {

  // Fields
  private String machineTypeUuid;

  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public MachineType(String name) {
    this.name = name;
  }
}
