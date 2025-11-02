package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.MachineStatus;
import java.util.List;
import java.util.Optional;

public interface IMachineStatusService {
  List<MachineStatus> getAllStatuses();

  MachineStatus getStatusByUuid(String uuid);

  MachineStatus createStatus(MachineStatus status);

  MachineStatus updateStatus(String uuid, MachineStatus updatedStatus);

  void deleteStatus(String uuid);

  Optional<MachineStatus> findByName(String name);
}
