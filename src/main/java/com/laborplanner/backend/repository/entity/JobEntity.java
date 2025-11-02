package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "job")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class JobEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "job_uuid", nullable = false, updatable = false)
  private UUID jobUuid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_template_uuid")
  private JobTemplateEntity template;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "duration", nullable = false)
  private Duration duration;

  @Column(name = "deadline")
  private LocalDateTime deadline;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "required_machine_type_uuid")
  private MachineTypeEntity requiredMachineType;

  // Self-referencing many-to-many for dependencies
  @ManyToMany
  @JoinTable(
      name = "job_dependencies",
      joinColumns = @JoinColumn(name = "job_uuid"),
      inverseJoinColumns = @JoinColumn(name = "dependency_uuid"))
  private List<JobEntity> dependencies = new ArrayList<>();
}
