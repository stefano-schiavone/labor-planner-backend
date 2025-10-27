package com.laborplanner.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class ScheduledJob {

  // Fields
  @Setter(AccessLevel.NONE)
  private String scheduledJobUuid = UUID.randomUUID().toString();

  private Schedule schedule;

  private Job job;

  private Machine machine;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  // Constructors
  // Constructor with all arguments except UUID
  public ScheduledJob(
      Schedule schedule, Job job, Machine machine, LocalDateTime startTime, LocalDateTime endTime) {
    this.schedule = schedule;
    this.job = job;
    this.machine = machine;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
