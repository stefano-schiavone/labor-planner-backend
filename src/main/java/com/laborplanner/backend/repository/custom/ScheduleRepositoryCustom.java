package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepositoryCustom {

  // Find schedules created by a specific user
  List<Schedule> findByCreatedByUser(User user);

  // Find schedule by its week start date (exact match)
  Optional<Schedule> findByWeekStartDate(LocalDateTime weekStartDate);

  // Check if a schedule already exists for a given week
  boolean existsByWeekStartDate(LocalDateTime weekStartDate);

  // Find schedules within a date range (useful for listing)
  List<Schedule> findByWeekStartDateBetween(LocalDateTime start, LocalDateTime end);

  // Get the latest modified schedule for a given user
  Optional<Schedule> findFirstByCreatedByUserOrderByLastModifiedDateDesc(User user);
}
