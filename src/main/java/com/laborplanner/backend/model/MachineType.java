package com.laborplanner.backend.model;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class MachineType {

  // Fields
  @Setter(AccessLevel.NONE)
  private String machineTypeUuid = UUID.randomUUID().toString();

  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public MachineType(String name) {
    this.name = name;
  }
}
