package com.laborplanner.backend.service;

import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.exception.job.DuplicateJobNameException;
import com.laborplanner.backend.exception.job.JobNotFoundException;
import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.service.interfaces.IJobService;
import com.laborplanner.backend.service.interfaces.IJobTemplateReadService;
import com.laborplanner.backend.service.interfaces.IMachineTypeReadService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class JobService implements IJobService {

  private final JobRepository jobRepository;
  private final IMachineTypeReadService machineTypeService;
  private final IJobTemplateReadService jobTemplateService;

  public JobService(
      JobRepository jobRepository,
      IMachineTypeReadService machineTypeService,
      IJobTemplateReadService jobTemplateService) {
    this.jobRepository = jobRepository;
    this.machineTypeService = machineTypeService;
    this.jobTemplateService = jobTemplateService;
  }

  // -------------------------------------------------------
  // IJobService Implementation
  // -------------------------------------------------------

  @Override
  public List<JobDto> getAllJobs() {
    return jobRepository.findAllByOrderByDeadlineAsc().stream().map(this::toDto).toList();
  }

  @Override
  public JobDto getJobByUuid(String uuid) {
    Job job =
        jobRepository
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
    Job existing =
        jobRepository
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
    existing.setDuration(dto.getDuration());
    existing.setDeadline(dto.getDeadline());
    existing.setRequiredMachineType(requiredType);
    existing.setTemplate(template);

    Job saved = jobRepository.update(existing);

    log.info("Job updated successfully: uuid='{}'", saved.getJobUuid());
    return toDto(saved);
  }

  @Override
  public void deleteJob(String uuid) {
    if (!jobRepository.existsByUuid(uuid)) {
      throw new JobNotFoundException(uuid);
    }
    jobRepository.deleteByUuid(uuid);
    log.info("Deleted job: uuid='{}'", uuid);
  }

  @Override
  public List<JobDto> findByRequiredMachineType(MachineType type) {
    return jobRepository.findByRequiredMachineType(type).stream().map(this::toDto).toList();
  }

  // -------------------------------------------------------
  // Mapping Helpers
  // -------------------------------------------------------

  private Job toModel(JobDto dto, JobTemplate template, MachineType requiredType) {
    Job job =
        new Job(
            dto.getName(),
            dto.getDescription(),
            dto.getDuration(),
            dto.getDeadline(),
            requiredType,
            template);

    job.setTemplate(template);
    return job;
  }

  private JobDto toDto(Job model) {
    String templateUuid =
        model.getTemplate() != null ? model.getTemplate().getJobTemplateUuid() : null;

    return new JobDto(
        model.getJobUuid(),
        templateUuid,
        model.getName(),
        model.getDescription(),
        model.getDuration(),
        model.getDeadline(),
        model.getRequiredMachineType().getMachineTypeUuid());
  }
}
