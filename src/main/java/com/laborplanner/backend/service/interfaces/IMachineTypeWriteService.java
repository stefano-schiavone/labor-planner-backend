package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.MachineType;

public interface IMachineTypeWriteService {
  MachineType createType(MachineType type);

  MachineType updateType(String uuid, MachineType updatedType);

  void deleteType(String uuid);
}
