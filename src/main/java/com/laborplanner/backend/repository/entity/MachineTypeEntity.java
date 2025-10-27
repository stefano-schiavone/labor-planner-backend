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
  @Column(name = "machine_type_uuid", nullable = false, updatable = false)
  private String machineTypeUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false, unique = true)
  private String name;
}
