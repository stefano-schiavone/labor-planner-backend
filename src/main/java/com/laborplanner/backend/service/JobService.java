package com.laborplanner.backend.service;

import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.exception.job.DuplicateJobNameException;
import com.laborplanner.backend.exception.job.JobNotFoundException;
import com.laborplanner.backend.exception.scheduledJob.ScheduledJobConflictException;
import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.repository.ScheduleRepository;
import com.laborplanner.backend.service.interfaces.IJobService;
import com.laborplanner.backend.service.interfaces.IJobTemplateReadService;
import com.laborplanner.backend.service.interfaces.IMachineTypeReadService;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class JobService implements IJobService {

   private final JobRepository jobRepository;
   private final ScheduleRepository scheduleRepository;
   private final IMachineTypeReadService machineTypeService;
   private final IJobTemplateReadService jobTemplateService;

   public JobService(
         JobRepository jobRepository,
         ScheduleRepository scheduleRepository,
         IMachineTypeReadService machineTypeService,
         IJobTemplateReadService jobTemplateService) {
      this.jobRepository = jobRepository;
      this.scheduleRepository = scheduleRepository;
      this.machineTypeService = machineTypeService;
      this.jobTemplateService = jobTemplateService;
   }

   @Override
   public List<JobDto> getAllJobs() {
      return jobRepository.findAllByOrderByDeadlineAsc().stream().map(this::toDto).toList();
   }

   @Override
   public JobDto getJobByUuid(String uuid) {
      Job job = jobRepository
            .findByUuid(uuid)
            .orElseThrow(
                  () -> {
                     log.warn("Job not found: {}", uuid);
                     return new JobNotFoundException(uuid);
                  });

      return toDto(job);
   }

   @Override
   public JobDto createJob(JobDto dto) {
      log.info("Creating job: name='{}'", dto.getName());

      if (jobRepository.existsByName(dto.getName())) {
         log.warn("Duplicate job name attempted: {}", dto.getName());
         throw new DuplicateJobNameException(dto.getName());
      }

      MachineType requiredType = machineTypeService.getTypeByUuid(dto.getRequiredMachineTypeUuid());

      JobTemplate template = null;
      if (dto.getTemplateUuid() != null) {
         template = jobTemplateService.getJobTemplateByUuid(dto.getTemplateUuid());
      }

      Job model = toModel(dto, template, requiredType);
      Job created = jobRepository.create(model);

      log.info("Job created successfully: uuid='{}'", created.getJobUuid());
      return toDto(created);
   }

   @Override
   public JobDto updateJob(String uuid, JobDto dto) {
      Job existing = jobRepository
            .findByUuid(uuid)
            .orElseThrow(
                  () -> {
                     log.warn("Job not found for update: uuid='{}'", uuid);
                     return new JobNotFoundException(uuid);
                  });

      // Name uniqueness check if changed
      if (!existing.getName().equals(dto.getName()) && jobRepository.existsByName(dto.getName())) {
         log.warn("Duplicate job name attempted during update: {}", dto.getName());
         throw new DuplicateJobNameException(dto.getName());
      }

      MachineType requiredType = machineTypeService.getTypeByUuid(dto.getRequiredMachineTypeUuid());

      JobTemplate template = null;
      if (dto.getTemplateUuid() != null) {
         template = jobTemplateService.getJobTemplateByUuid(dto.getTemplateUuid());
      }

      // Apply updates
      existing.setName(dto.getName());
      existing.setDescription(dto.getDescription());
      existing.setDurationMinutes(dto.getDurationMinutes());
      existing.setDeadline(dto.getDeadline());
      existing.setRequiredMachineType(requiredType);
      existing.setTemplate(template);

      Job saved = jobRepository.update(existing);

      log.info("Job updated successfully: uuid='{}'", saved.getJobUuid());
      return toDto(saved);
   }

   @Override
   public void deleteJob(String uuid) {
      log.info("Attempting to delete job: uuid='{}'", uuid);

      if (!jobRepository.existsByUuid(uuid)) {
         log.warn("Job not found: {}", uuid);
         throw new JobNotFoundException(uuid);
      }

      // Check if job is part of any schedule
      try {
         Optional<Schedule> conflictingSchedule = scheduleRepository.findScheduleContainingJob(uuid);
         if (conflictingSchedule.isPresent()) {
            Schedule schedule = conflictingSchedule.get();
            log.warn("Job {} is part of schedule {}", uuid, schedule.getScheduleUuid());
            throw new ScheduledJobConflictException(uuid, schedule.getScheduleUuid());
         }
      } catch (ScheduledJobConflictException e) {
         // Re-throw the conflict exception
         throw e;
      } catch (Exception e) {
         log.error("Error checking for schedule conflicts: {}", e.getMessage(), e);
         // Continue - will be caught by DB constraint if needed
      }

      try {
         jobRepository.deleteByUuid(uuid);
         log.info("Deleted job: uuid='{}'", uuid);
      } catch (Exception e) {
         log.error("Failed to delete job {}: {}", uuid, e.getMessage(), e);

         // Check if it's a foreign key constraint violation
         String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
         if (errorMsg.contains("foreign key") || errorMsg.contains("fktg2i8rwld0yc9jn0s4kixiawk")) {
            // Try to find the schedule again as a fallback
            try {
               Optional<Schedule> schedule = scheduleRepository.findScheduleContainingJob(uuid);
               if (schedule.isPresent()) {
                  log.warn("Found conflicting schedule on FK error:  {}", schedule.get().getScheduleUuid());
                  throw new ScheduledJobConflictException(uuid, schedule.get().getScheduleUuid());
               }
            } catch (ScheduledJobConflictException ex) {
               throw ex;
            } catch (Exception ex) {
               log.error("Could not determine conflicting schedule", ex);
            }
         }

         throw new IllegalStateException("Failed to delete job:  " + e.getMessage(), e);
      }
   }

   @Override
   public List<JobDto> findByRequiredMachineType(MachineType type) {
      return jobRepository.findByRequiredMachineType(type).stream().map(this::toDto).toList();
   }

   public List<JobDto> findJobsInDeadlineRange(LocalDateTime start, LocalDateTime end) {
      return jobRepository.findByDeadlineBetween(start, end)
            .stream()
            .map(this::toDto)
            .toList();
   }

   // -------------------------------------------------------
   // Mapping Helpers
   // -------------------------------------------------------

   private Job toModel(JobDto dto, JobTemplate template, MachineType requiredType) {
      Job job = new Job(
            dto.getJobUuid(),
            dto.getName(),
            dto.getDescription(),
            dto.getDurationMinutes(),
            dto.getDeadline(),
            requiredType,
            template);

      job.setTemplate(template);
      return job;
   }

   private JobDto toDto(Job model) {
      String templateUuid = model.getTemplate() != null ? model.getTemplate().getJobTemplateUuid() : null;

      return new JobDto(
            model.getJobUuid(),
            templateUuid,
            model.getName(),
            model.getDescription(),
            model.getDurationMinutes(),
            model.getDeadline(),
            model.getRequiredMachineType().getMachineTypeUuid());
   }
}
