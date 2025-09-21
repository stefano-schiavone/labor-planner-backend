package com.laborplanner.backend.model;

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
public class JobTemplate {

  // Fields
  @Id
  @Column(name = "job_template_uuid", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String jobTemplateUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "duration", nullable = false)
  private Duration duration;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "required_machine_type_uuid", nullable = false)
  private MachineType requiredMachineType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_uuid", nullable = false)
  private User createdByUser;

  // Constructors
  // Constructor with all arguments except UUID
  public JobTemplate(
      String name,
      String description,
      Duration duration,
      MachineType requiredMachineType,
      User createdByUser) {
    this.name = name;
    this.description = description;
    this.duration = duration;
    this.requiredMachineType = requiredMachineType;
    this.createdByUser = createdByUser;
  }
}
