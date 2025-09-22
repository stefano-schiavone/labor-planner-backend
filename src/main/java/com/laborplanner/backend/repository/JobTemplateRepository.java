package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTemplateRepository extends JpaRepository<JobTemplate, String> {

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
