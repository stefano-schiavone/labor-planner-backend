package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "machine_type")
public class MachineType {

  @Id
  @Column(name = "machine_type_uuid", nullable = false, updatable = false)
  private String machineTypeUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  // Constructors
  public MachineType() {}

  public MachineType(String name) {
    this.name = name;
  }

  // Getters & Setters
  public String getMachineTypeUuid() {
    return machineTypeUuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
