package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.time.Duration;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "job_template")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class JobTemplateEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "job_template_uuid", nullable = false, updatable = false)
  private UUID jobTemplateUuid;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "duration", nullable = false)
  private Duration duration;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "required_machine_type_uuid", nullable = false)
  private MachineTypeEntity requiredMachineType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_uuid", nullable = false)
  private UserEntity createdByUser;
}
