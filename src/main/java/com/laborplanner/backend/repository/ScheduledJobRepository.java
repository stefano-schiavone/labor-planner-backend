package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.ScheduledJob;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, String> {

  // Find all jobs in a given schedule
  List<ScheduledJob> findBySchedule(Schedule schedule);

  // Find all jobs assigned to a machine
  List<ScheduledJob> findByMachine(Machine machine);

  // Find all jobs of a specific job type
  List<ScheduledJob> findByJob(Job job);

  // Check if a machine is busy during a time period
  boolean existsByMachineAndStartTimeBeforeAndEndTimeAfter(
      Machine machine, LocalDateTime end, LocalDateTime start);

  // Find all jobs scheduled in a time range
  List<ScheduledJob> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
