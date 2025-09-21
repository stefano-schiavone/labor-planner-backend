package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "machine")
public class Machine {

  // Fields
  @Id
  @Column(name = "machine_uuid", nullable = false, updatable = false)
  private String machineUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "machine_type_uuid", nullable = false)
  private MachineType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "machine_status_uuid", nullable = false)
  private MachineStatus status;

  // Constructors
  public Machine() {}

  public Machine(String name, String description, MachineType type, MachineStatus status) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.status = status;
  }

  // Getters & Setters
  public String getMachineUuid() {
    return this.machineUuid;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public MachineType getType() {
    return this.type;
  }

  public void setType(MachineType type) {
    this.type = type;
  }

  public MachineStatus getStatus() {
    return this.status;
  }

  public void setStatus(MachineStatus status) {
    this.status = status;
  }
}
