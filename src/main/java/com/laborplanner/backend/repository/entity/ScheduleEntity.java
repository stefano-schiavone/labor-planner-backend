package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class ScheduleEntity {

  // Fields
  @Id
  @Column(name = "schedule_uuid", nullable = false, updatable = false)
  private String scheduleUuid = UUID.randomUUID().toString();

  @Column(name = "week_start_date", nullable = false)
  private LocalDateTime weekStartDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_uuid", nullable = false)
  private UserEntity createdByUser;

  @Column(name = "last_modified_date", nullable = false)
  private LocalDateTime lastModifiedDate;
}
