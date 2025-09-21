package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "schedule")
public class Schedule {

  // Fields
  @Id
  @Column(name = "schedule_uuid", nullable = false, updatable = false)
  private String scheduleUuid = UUID.randomUUID().toString();

  @Column(name = "week_start_date", nullable = false)
  private LocalDateTime weekStartDate;

  // Many schedules can be created by one user
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_uuid", nullable = false)
  private User createdByUser;

  @Column(name = "last_modified_date", nullable = false)
  private LocalDateTime lastModifiedDate;

  // Constructors
  public Schedule() {}

  public Schedule(LocalDateTime weekStartDate, User createdByUser, LocalDateTime lastModifiedDate) {
    this.weekStartDate = weekStartDate;
    this.createdByUser = createdByUser;
    this.lastModifiedDate = lastModifiedDate;
  }

  // Getters & Setters
  public String getScheduleUuid() {
    return this.scheduleUuid;
  }

  public LocalDateTime getWeekStartDate() {
    return this.weekStartDate;
  }

  public void setWeekStartDate(LocalDateTime weekStartDate) {
    this.weekStartDate = weekStartDate;
  }

  public User getCreatedByUser() {
    return this.createdByUser;
  }

  public void setCreatedByUser(User createdByUser) {
    this.createdByUser = createdByUser;
  }

  public LocalDateTime getLastModifiedDate() {
    return this.lastModifiedDate;
  }

  public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }
}
