package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.model.MachineType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineTypeRepositoryCustom {

  // Find by exact name
  Optional<MachineType> findByName(String name);

  // Check if a machine type already exists
  boolean existsByName(String name);

  // List all machine types sorted by name
  List<MachineType> findAllByOrderByNameAsc();
}
