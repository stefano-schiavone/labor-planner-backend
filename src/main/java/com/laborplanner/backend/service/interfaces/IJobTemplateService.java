package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.User;
import java.util.List;
import java.util.Optional;

public interface IJobTemplateService {
  List<JobTemplate> getAllJobTemplates();

  JobTemplate getJobTemplateByUuid(String uuid);

  JobTemplate createJobTemplate(JobTemplate jobTemplate);

  JobTemplate updateJobTemplate(String uuid, JobTemplate updatedJobTemplate);

  void deleteJobTemplate(String uuid);

  Optional<JobTemplate> findByName(String name);

  List<JobTemplate> findByRequiredMachineType(MachineType machineType);

  List<JobTemplate> findByCreatedByUser(User user);
}
