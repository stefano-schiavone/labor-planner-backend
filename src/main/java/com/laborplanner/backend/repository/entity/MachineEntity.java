package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "machine")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class MachineEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "machine_uuid", nullable = false, updatable = false)
  private UUID machineUuid;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "machine_type_uuid", nullable = false)
  private MachineTypeEntity type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "machine_status_uuid", nullable = false)
  private MachineStatusEntity status;
}
