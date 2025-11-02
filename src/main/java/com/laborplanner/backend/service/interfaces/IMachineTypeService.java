package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.MachineType;
import java.util.List;
import java.util.Optional;

public interface IMachineTypeService {
  List<MachineType> getAllTypes();

  MachineType getTypeByUuid(String uuid);

  MachineType createType(MachineType type);

  MachineType updateType(String uuid, MachineType updatedType);

  void deleteType(String uuid);

  Optional<MachineType> findByName(String name);
}
