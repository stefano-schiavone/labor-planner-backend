package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "machine_status")
public class MachineStatus {

  // Fields
  @Id
  @Column(name = "machine_status_uuid", nullable = false, updatable = false)
  private String machineUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false)
  private String name = UUID.randomUUID().toString();

  // Constructors
  public MachineStatus() {}

  public MachineStatus(String name) {
    this.name = name;
  }

  // Getters & Setters
  public String getMachineStatusUuid() {
    return this.machineUuid;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
