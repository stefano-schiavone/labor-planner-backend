package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.dto.machine.MachineDto;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.model.MachineType;
import java.util.List;
import java.util.Optional;

public interface IMachineService {

  List<MachineDto> getAllMachines();

  MachineDto getMachineByUuid(String uuid);

  MachineDto createMachine(MachineDto machine);

  MachineDto updateMachine(String uuid, MachineDto updatedMachine);

  void deleteMachine(String uuid);

  Optional<Machine> findByName(String name);

  List<MachineDto> findByType(MachineType type);

  List<MachineDto> findByStatus(MachineStatus status);

  List<MachineDto> findByTypeAndStatus(MachineType type, MachineStatus status);
}
