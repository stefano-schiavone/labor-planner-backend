package com.laborplanner.backend.service;

import com.laborplanner.backend.exception.job.DuplicateJobTemplateNameException;
import com.laborplanner.backend.exception.job.JobTemplateNotFoundException;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.JobTemplateRepository;
import com.laborplanner.backend.service.interfaces.IJobTemplateService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobTemplateService implements IJobTemplateService {

  private final JobTemplateRepository jobTemplateRepository;

  public JobTemplateService(JobTemplateRepository jobTemplateRepository) {
    this.jobTemplateRepository = jobTemplateRepository;
  }

  // ---------------------------
  // IJobTemplateService Implementation
  // ---------------------------

  @Override
  public List<JobTemplate> getAllJobTemplates() {
    return jobTemplateRepository.findAllByOrderByNameAsc();
  }

  @Override
  public JobTemplate getJobTemplateByUuid(String uuid) {
    return jobTemplateRepository
        .findByUuid(uuid)
        .orElseThrow(
            () -> {
              log.warn("JobTemplate not found: {}", uuid);
              return new JobTemplateNotFoundException(uuid);
            });
  }

  @Override
  public JobTemplate createJobTemplate(JobTemplate jobTemplate) {
    log.info("Creating job template: name='{}'", jobTemplate.getName());

    if (jobTemplateRepository.existsByName(jobTemplate.getName())) {
      log.warn("Duplicate job template name attempted: {}", jobTemplate.getName());
      throw new DuplicateJobTemplateNameException(jobTemplate.getName());
    }

    JobTemplate created = jobTemplateRepository.create(jobTemplate);
    log.info("JobTemplate created successfully: uuid='{}'", created.getJobTemplateUuid());
    return created;
  }

  @Override
  public JobTemplate updateJobTemplate(String uuid, JobTemplate updatedJobTemplate) {
    JobTemplate existing =
        jobTemplateRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("JobTemplate not found for update: uuid='{}'", uuid);
                  return new JobTemplateNotFoundException(uuid);
                });

    existing.setName(updatedJobTemplate.getName());
    existing.setDescription(updatedJobTemplate.getDescription());
    existing.setDuration(updatedJobTemplate.getDuration());
    existing.setRequiredMachineType(updatedJobTemplate.getRequiredMachineType());
    existing.setCreatedByUser(updatedJobTemplate.getCreatedByUser());

    JobTemplate saved = jobTemplateRepository.create(existing);
    log.info("JobTemplate updated successfully: uuid='{}'", saved.getJobTemplateUuid());

    return saved;
  }

  @Override
  public void deleteJobTemplate(String uuid) {
    if (!jobTemplateRepository.existsByUuid(uuid)) {
      throw new JobTemplateNotFoundException(uuid);
    }
    jobTemplateRepository.deleteByUuid(uuid);
    log.info("JobTemplate deleted successfully: uuid='{}'", uuid);
  }

  @Override
  public Optional<JobTemplate> findByName(String name) {
    return jobTemplateRepository.findByName(name);
  }

  @Override
  public List<JobTemplate> findByRequiredMachineType(MachineType machineType) {
    return jobTemplateRepository.findByRequiredMachineType(machineType);
  }

  @Override
  public List<JobTemplate> findByCreatedByUser(User user) {
    return jobTemplateRepository.findByCreatedByUser(user);
  }
}
