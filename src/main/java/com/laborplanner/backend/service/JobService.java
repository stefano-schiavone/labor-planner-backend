package com.laborplanner.backend.service;

import com.laborplanner.backend.exception.job.DuplicateJobNameException;
import com.laborplanner.backend.exception.job.JobNotFoundException;
import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.service.interfaces.IJobService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobService implements IJobService {

  private final JobRepository jobRepository;

  public JobService(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  @Override
  public List<Job> getAllJobs() {
    return jobRepository.findAllByOrderByDeadlineAsc();
  }

  @Override
  public Job getJobByUuid(String uuid) {
    return jobRepository
        .findByUuid(uuid)
        .orElseThrow(
            () -> {
              log.warn("Job not found: {}", uuid);
              return new JobNotFoundException(uuid);
            });
  }

  @Override
  public Job createJob(Job job) {
    log.info("Creating job: name='{}'", job.getName());

    if (jobRepository.existsByName(job.getName())) {
      log.warn("Duplicate job name attempted: {}", job.getName());
      throw new DuplicateJobNameException(job.getName());
    }

    Job created = jobRepository.create(job);
    log.info("Job created successfully: uuid='{}'", created.getJobUuid());
    return created;
  }

  @Override
  public Job updateJob(String uuid, Job updatedJob) {
    Job existing =
        jobRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("Job not found for update: uuid='{}'", uuid);
                  return new JobNotFoundException(uuid);
                });

    existing.setName(updatedJob.getName());
    existing.setDescription(updatedJob.getDescription());
    existing.setDuration(updatedJob.getDuration());
    existing.setDeadline(updatedJob.getDeadline());
    existing.setTemplate(updatedJob.getTemplate());
    existing.setRequiredMachineType(updatedJob.getRequiredMachineType());
    existing.setDependencies(updatedJob.getDependencies());

    Job saved = jobRepository.update(existing);
    log.info("Job updated successfully: uuid='{}'", saved.getJobUuid());
    return saved;
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
  public Optional<Job> findByName(String name) {
    return jobRepository.findByName(name);
  }

  @Override
  public List<Job> findByTemplate(JobTemplate template) {
    return jobRepository.findByTemplate(template);
  }

  @Override
  public List<Job> findByRequiredMachineType(MachineType type) {
    return jobRepository.findByRequiredMachineType(type);
  }

  @Override
  public List<Job> findByDeadlineBefore(LocalDateTime deadline) {
    return jobRepository.findByDeadlineBefore(deadline);
  }

  @Override
  public List<Job> findByDeadlineAfter(LocalDateTime deadline) {
    return jobRepository.findByDeadlineAfter(deadline);
  }
}
