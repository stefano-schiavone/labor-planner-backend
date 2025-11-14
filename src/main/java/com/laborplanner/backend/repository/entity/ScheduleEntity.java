package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
public class ScheduleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "schedule_uuid", nullable = false, updatable = false)
  private UUID scheduleUuid;

  @Column(name = "week_start_date", nullable = false)
  private LocalDateTime weekStartDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_uuid", nullable = false)
  private UserEntity createdByUser;

  @Column(name = "last_modified_date", nullable = false)
  private LocalDateTime lastModifiedDate;

  // One-to-many relationship with ScheduledJobEntity
  @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ScheduledJobEntity> scheduledJobList;
}
