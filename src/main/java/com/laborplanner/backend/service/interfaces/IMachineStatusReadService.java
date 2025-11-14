package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.MachineStatus;
import java.util.List;
import java.util.Optional;

public interface IMachineStatusReadService {
  List<MachineStatus> getAllStatuses();

  MachineStatus getStatusByUuid(String uuid);

  Optional<MachineStatus> findByName(String name);
}
