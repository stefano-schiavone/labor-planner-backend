package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "machine_status")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class MachineStatus {

  // Fields
  @Id
  @Column(name = "machine_status_uuid", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String machineStatusUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public MachineStatus(String name) {
    this.name = name;
  }
}
