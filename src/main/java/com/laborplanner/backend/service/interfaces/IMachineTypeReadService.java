package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.MachineType;
import java.util.List;
import java.util.Optional;

public interface IMachineTypeReadService {
  List<MachineType> getAllTypes();

  MachineType getTypeByUuid(String uuid);

  Optional<MachineType> findByName(String name);
}
