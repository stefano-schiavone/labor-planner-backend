package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "job_template")
public class JobTemplate {

  // Fields
  @Id
  @Column(name = "job_template_uuid", nullable = false, updatable = false)
  private String jobTemplateUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  // Duration represented as java.time.Duration
  @Column(name = "duration", nullable = false)
  private Duration duration;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "required_machine_type_uuid", nullable = false)
  private MachineType requiredMachineType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_uuid", nullable = false)
  private User createdByUser;

  // Constructors
  public JobTemplate() {}

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

  // Getters & Setters
  public String getJobTemplateUuid() {
    return this.jobTemplateUuid;
  }

  public String getName() {
    return name;
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

  public MachineType getRequiredMachineType() {
    return this.requiredMachineType;
  }

  public void setRequiredMachineType(MachineType requiredMachineType) {
    this.requiredMachineType = requiredMachineType;
  }

  public User getCreatedByUser() {
    return this.createdByUser;
  }

  public void setCreatedByUser(User createdByUser) {
    this.createdByUser = createdByUser;
  }
}
