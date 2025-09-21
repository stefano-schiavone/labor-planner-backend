package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "machine_type")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class MachineType {

  // Fields
  @Id
  @Column(name = "machine_type_uuid", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String machineTypeUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public MachineType(String name) {
    this.name = name;
  }
}
