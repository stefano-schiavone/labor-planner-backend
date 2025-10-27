package com.laborplanner.backend.model;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class MachineStatus {

  // Fields
  @Setter(AccessLevel.NONE)
  private String machineStatusUuid = UUID.randomUUID().toString();

  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public MachineStatus(String name) {
    this.name = name;
  }
}
