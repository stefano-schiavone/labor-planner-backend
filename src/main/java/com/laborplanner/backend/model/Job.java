package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "job")
public class Job {

  // Fields
  @Id
  @Column(name = "job_uuid", nullable = false, updatable = false)
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
  public Job() {}

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

  // Getters & Setters
  public String getJobUuid() {
    return this.jobUuid;
  }

  public JobTemplate getTemplate() {
    return this.template;
  }

  public void setTemplate(JobTemplate template) {
    this.template = template;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Duration getDuration() {
    return this.duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }

  public LocalDateTime getDeadline() {
    return this.deadline;
  }

  public void setDeadline(LocalDateTime deadline) {
    this.deadline = deadline;
  }

  public MachineType getRequiredMachineType() {
    return this.requiredMachineType;
  }

  public void setRequiredMachineType(MachineType requiredMachineType) {
    this.requiredMachineType = requiredMachineType;
  }

  public List<Job> getDependencies() {
    return this.dependencies;
  }

  public void addDependency(Job dependency) {
    this.dependencies.add(dependency);
  }
}
