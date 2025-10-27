package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.model.MachineType;
import java.util.List;
import java.util.Optional;

public interface IMachineService {

  List<Machine> getAllMachines();

  Machine getMachineByUuid(String uuid);

  Machine createMachine(Machine machine);

  Machine updateMachine(String uuid, Machine updatedMachine);

  void deleteMachine(String uuid);

  Optional<Machine> findByName(String name);

  List<Machine> findByType(MachineType type);

  List<Machine> findByStatus(MachineStatus status);

  List<Machine> findByTypeAndStatus(MachineType type, MachineStatus status);
}
