package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.model.MachineType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, String> {

  // Find machine by exact name
  Optional<Machine> findByName(String name);

  // Check if a machine with a given name exists
  boolean existsByName(String name);

  // Find all machines of a given type
  List<Machine> findByType(MachineType type);

  // Find all machines with a given status
  List<Machine> findByStatus(MachineStatus status);

  // Find all machines of a type and status (e.g., "CNC" + "Available")
  List<Machine> findByTypeAndStatus(MachineType type, MachineStatus status);

  // List all machines ordered alphabetically
  List<Machine> findAllByOrderByNameAsc();
}
