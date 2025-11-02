package com.laborplanner.backend.service;

import com.laborplanner.backend.exception.machine.DuplicateMachineNameException;
import com.laborplanner.backend.exception.machine.MachineNotFoundException;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.MachineRepository;
import com.laborplanner.backend.service.interfaces.IMachineService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MachineService implements IMachineService {

  private final MachineRepository machineRepository;

  public MachineService(MachineRepository machineRepository) {
    this.machineRepository = machineRepository;
  }

  // ---------------------------
  // IMachineService Implementation
  // ---------------------------

  @Override
  public List<Machine> getAllMachines() {
    return machineRepository.findAllByOrderByNameAsc();
  }

  @Override
  public Machine getMachineByUuid(String uuid) {
    return machineRepository
        .findByUuid(uuid)
        .orElseThrow(
            () -> {
              log.warn("Machine not found: {}", uuid);
              return new MachineNotFoundException(uuid);
            });
  }

  @Override
  public Machine createMachine(Machine machine) {
    log.info("Creating machine: name='{}', type='{}'", machine.getName(), machine.getType());
    // TODO: Validate Uniqueness of everything
    if (machineRepository.existsByName(machine.getName())) {
      log.warn("Duplicate machine name attempted: {}", machine.getName());
      throw new DuplicateMachineNameException(machine.getName());
    }
    Machine created = machineRepository.create(machine);
    log.info("Machine created successfully: uuid='{}'", created.getMachineUuid());
    return created;
  }

  @Override
  public Machine updateMachine(String uuid, Machine updatedMachine) {
    Machine existing =
        machineRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("Machine not found for update: uuid='{}'", uuid);
                  return new MachineNotFoundException(uuid);
                });

    existing.setName(updatedMachine.getName());
    existing.setDescription(updatedMachine.getDescription());
    existing.setType(updatedMachine.getType());
    existing.setStatus(updatedMachine.getStatus());

    Machine saved = machineRepository.update(existing);
    log.info("Machine updated successfully: uuid='{}'", saved.getMachineUuid());

    return saved;
  }

  @Override
  public void deleteMachine(String uuid) {
    if (!machineRepository.existsByUuid(uuid)) {
      throw new MachineNotFoundException(uuid);
    }
    machineRepository.deleteByUuid(uuid);
  }

  // NOTE: To be implemented in repo
  @Override
  public Optional<Machine> findByName(String name) {
    return machineRepository.findByName(name);
  }

  // NOTE: To be implemented in repo
  @Override
  public List<Machine> findByType(MachineType type) {
    return machineRepository.findByType(type);
  }

  // NOTE: To be implemented in repo
  @Override
  public List<Machine> findByStatus(MachineStatus status) {
    return machineRepository.findByStatus(status);
  }

  // NOTE: To be implemented in repo
  @Override
  public List<Machine> findByTypeAndStatus(MachineType type, MachineStatus status) {
    return machineRepository.findByTypeAndStatus(type, status);
  }
}
