package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.model.MachineStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineStatusRepositoryCustom {

  // Find a status by name (e.g., "Available", "Maintenance", etc.)
  Optional<MachineStatus> findByName(String name);

  // Check if a status exists
  boolean existsByName(String name);

  // Get all statuses in alphabetical order
  List<MachineStatus> findAllByOrderByNameAsc();
}
