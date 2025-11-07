package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.MachineStatus;

public interface IMachineStatusWriteService {
  MachineStatus createStatus(MachineStatus status);

  MachineStatus updateStatus(String uuid, MachineStatus updatedStatus);

  void deleteStatus(String uuid);
}
