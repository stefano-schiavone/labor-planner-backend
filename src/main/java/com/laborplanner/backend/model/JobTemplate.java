package com.laborplanner.backend.model;

import java.time.Duration;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class JobTemplate {

  // Fields
  @Setter(AccessLevel.NONE)
  private String jobTemplateUuid = UUID.randomUUID().toString();

  private String name;

  private String description;

  private Duration duration;

  private MachineType requiredMachineType;

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
