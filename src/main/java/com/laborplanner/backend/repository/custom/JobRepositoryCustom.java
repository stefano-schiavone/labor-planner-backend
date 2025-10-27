package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepositoryCustom {

  // Find a job by exact name
  Optional<Job> findByName(String name);

  // Check if a job with the given name exists
  boolean existsByName(String name);

  // Find all jobs created from a specific template
  List<Job> findByTemplate(JobTemplate template);

  // Find all jobs requiring a specific machine type
  List<Job> findByRequiredMachineType(MachineType machineType);

  // Find all jobs with deadlines before a certain date (overdue jobs)
  List<Job> findByDeadlineBefore(LocalDateTime deadline);

  // Find all jobs with deadlines after a certain date (upcoming jobs)
  List<Job> findByDeadlineAfter(LocalDateTime deadline);

  // List jobs ordered by nearest deadline
  List<Job> findAllByOrderByDeadlineAsc();
}
