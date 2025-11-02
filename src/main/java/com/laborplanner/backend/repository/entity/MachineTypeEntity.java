package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "machine_type")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class MachineTypeEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "machine_type_uuid", nullable = false, updatable = false)
  private UUID machineTypeUuid;

  @Column(name = "name", nullable = false, unique = true)
  private String name;
}
