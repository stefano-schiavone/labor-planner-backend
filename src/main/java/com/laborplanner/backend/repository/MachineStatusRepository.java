package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.MachineStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineStatusRepository extends JpaRepository<MachineStatus, String> {

  // Find a status by name (e.g., "Available", "Maintenance", etc.)
  Optional<MachineStatus> findByName(String name);

  // Check if a status exists
  boolean existsByName(String name);

  // Get all statuses in alphabetical order
  List<MachineStatus> findAllByOrderByNameAsc();
}
