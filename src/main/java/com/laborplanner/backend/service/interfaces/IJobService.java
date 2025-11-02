package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IJobService {
  List<Job> getAllJobs();

  Job getJobByUuid(String uuid);

  Job createJob(Job job);

  Job updateJob(String uuid, Job updatedJob);

  void deleteJob(String uuid);

  Optional<Job> findByName(String name);

  List<Job> findByTemplate(JobTemplate template);

  List<Job> findByRequiredMachineType(MachineType type);

  List<Job> findByDeadlineBefore(LocalDateTime deadline);

  List<Job> findByDeadlineAfter(LocalDateTime deadline);
}
