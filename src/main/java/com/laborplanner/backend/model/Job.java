package com.laborplanner.backend.model;

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
public class Job {

  // Fields
  @Id
  @Column(name = "job_uuid", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String jobUuid = UUID.randomUUID().toString();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_template_uuid")
  private JobTemplate template;

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
  private MachineType requiredMachineType;

  // Self-referencing many-to-many for dependencies
  @ManyToMany
  @JoinTable(
      name = "job_dependencies",
      joinColumns = @JoinColumn(name = "job_uuid"),
      inverseJoinColumns = @JoinColumn(name = "dependency_uuid"))
  private List<Job> dependencies = new ArrayList<>();

  // Constructors
  // Constructor with all arguments except UUID
  public Job(
      String name,
      String description,
      Duration duration,
      LocalDateTime deadline,
      MachineType requiredMachineType,
      JobTemplate template) {
    this.name = name;
    this.description = description;
    this.duration = duration;
    this.deadline = deadline;
    this.requiredMachineType = requiredMachineType;
    this.template = template;
  }

  // Dependency helper
  public void addDependency(Job dependency) {
    this.dependencies.add(dependency);
  }
}
