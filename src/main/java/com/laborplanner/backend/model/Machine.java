package com.laborplanner.backend.model;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class Machine {

  // Fields
  @Setter(AccessLevel.NONE)
  private String machineUuid = UUID.randomUUID().toString();

  private String name;

  private String description;

  private MachineType type;

  private MachineStatus status;

  // Constructors
  // Constructor with all arguments except UUID
  public Machine(String name, String description, MachineType type, MachineStatus status) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.status = status;
  }
}
