package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTemplateRepositoryCustom {

  // Find template by exact name
  Optional<JobTemplate> findByName(String name);

  // Check if a template with a given name exists
  boolean existsByName(String name);

  // Find all templates for a given machine type
  List<JobTemplate> findByRequiredMachineType(MachineType machineType);

  // Find all templates created by a specific user
  List<JobTemplate> findByCreatedByUser(User user);

  // List all templates ordered alphabetically
  List<JobTemplate> findAllByOrderByNameAsc();
}
