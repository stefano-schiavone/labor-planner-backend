package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "machine_status")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class MachineStatusEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "machine_status_uuid", nullable = false, updatable = false)
  private UUID machineStatusUuid;

  @Column(name = "name", nullable = false, unique = true)
  private String name;
}
