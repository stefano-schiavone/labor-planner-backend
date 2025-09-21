package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "scheduled_job")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class ScheduledJob {

  // Fields
  @Id
  @Column(name = "scheduled_job_uuid", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String scheduledJobUuid = UUID.randomUUID().toString();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "schedule_uuid", nullable = false)
  private Schedule schedule;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_uuid", nullable = false)
  private Job job;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "machine_uuid", nullable = false)
  private Machine machine;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
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
