package com.laborplanner.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class Schedule {

  // Fields
  @Setter(AccessLevel.NONE)
  private String scheduleUuid = UUID.randomUUID().toString();

  private LocalDateTime weekStartDate;

  private User createdByUser;

  private LocalDateTime lastModifiedDate;

  // Constructors
  // Constructor with all arguments except UUID
  public Schedule(LocalDateTime weekStartDate, User createdByUser, LocalDateTime lastModifiedDate) {
    this.weekStartDate = weekStartDate;
    this.createdByUser = createdByUser;
    this.lastModifiedDate = lastModifiedDate;
  }
}
