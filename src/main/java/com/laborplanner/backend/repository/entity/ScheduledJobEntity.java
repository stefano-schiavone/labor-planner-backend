package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "scheduled_job")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class ScheduledJobEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "scheduled_job_uuid", nullable = false, updatable = false)
  private UUID scheduledJobUuid;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_uuid", nullable = false)
  private JobEntity job;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "machine_uuid", nullable = false)
  private MachineEntity machine;

  @Column(name = "starting_time_grain", nullable = false)
  private int startingTimeGrainIndex;

  // NOTE: Only here for Hibernate error
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedule_uuid", nullable = false)
  private ScheduleEntity schedule;
}
