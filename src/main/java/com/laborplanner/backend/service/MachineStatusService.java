package com.laborplanner.backend.service;

import com.laborplanner.backend.exception.machine.DuplicateMachineStatusNameException;
import com.laborplanner.backend.exception.machine.MachineStatusNotFoundException;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.repository.MachineStatusRepository;
import com.laborplanner.backend.service.interfaces.IMachineStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class MachineStatusService implements IMachineStatusService {

  private final MachineStatusRepository machineStatusRepository;

  public MachineStatusService(MachineStatusRepository machineStatusRepository) {
    this.machineStatusRepository = machineStatusRepository;
  }

  @Override
  public List<MachineStatus> getAllStatuses() {
    return machineStatusRepository.findAllByOrderByNameAsc();
  }

  @Override
  public MachineStatus getStatusByUuid(String uuid) {
    return machineStatusRepository
        .findByUuid(uuid)
        .orElseThrow(
            () -> {
              log.warn("Machine status not found: {}", uuid);
              return new MachineStatusNotFoundException(uuid);
            });
  }

  @Override
  public MachineStatus createStatus(MachineStatus status) {
    log.info("Creating machine status: name='{}'", status.getName());

    if (machineStatusRepository.existsByName(status.getName())) {
      log.warn("Duplicate machine status name attempted: {}", status.getName());
      throw new DuplicateMachineStatusNameException(status.getName());
    }

    MachineStatus created = machineStatusRepository.create(status);
    log.info("Machine status created successfully: uuid='{}'", created.getMachineStatusUuid());
    return created;
  }

  @Override
  public MachineStatus updateStatus(String uuid, MachineStatus updatedStatus) {
    MachineStatus existing =
        machineStatusRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("Machine status not found for update: uuid='{}'", uuid);
                  return new MachineStatusNotFoundException(uuid);
                });

    existing.setName(updatedStatus.getName());
    MachineStatus saved = machineStatusRepository.update(existing);
    log.info("Machine status updated successfully: uuid='{}'", saved.getMachineStatusUuid());
    return saved;
  }

  @Override
  public void deleteStatus(String uuid) {
    if (!machineStatusRepository.existsByUuid(uuid)) {
      throw new MachineStatusNotFoundException(uuid);
    }
    machineStatusRepository.deleteByUuid(uuid);
    log.info("Deleted machine status: uuid='{}'", uuid);
  }

  @Override
  public Optional<MachineStatus> findByName(String name) {
    return machineStatusRepository.findByName(name);
  }
}
