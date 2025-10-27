package com.laborplanner.backend.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

  // Fields
  @Setter(AccessLevel.NONE)
  private String jobUuid = UUID.randomUUID().toString();

  private JobTemplate template;

  private String name;

  private String description;

  private Duration duration;

  private LocalDateTime deadline;

  private MachineType requiredMachineType;

  // Self-referencing many-to-many for dependencies
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
