package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.model.MachineType;

import java.time.LocalDateTime;
import java.util.List;

public interface IJobService {
   List<JobDto> getAllJobs();

   JobDto getJobByUuid(String uuid);

   JobDto createJob(JobDto job);

   JobDto updateJob(String uuid, JobDto dto);

   void deleteJob(String uuid);

   List<JobDto> findByRequiredMachineType(MachineType type);

   List<JobDto> findJobsInDeadlineRange(LocalDateTime start, LocalDateTime end);
}
