package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "machine")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class Machine {

  // Fields
  @Id
  @Column(name = "machine_uuid", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
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
  // Constructor with all arguments except UUID
  public Machine(String name, String description, MachineType type, MachineStatus status) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.status = status;
  }
}
