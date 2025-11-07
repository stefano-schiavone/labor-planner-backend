package com.laborplanner.backend.service;

import com.laborplanner.backend.dto.machine.MachineDto;
import com.laborplanner.backend.exception.machine.DuplicateMachineNameException;
import com.laborplanner.backend.exception.machine.MachineNotFoundException;
import com.laborplanner.backend.exception.machine.NoMachineTypesExistException;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.MachineRepository;
import com.laborplanner.backend.service.interfaces.IMachineService;
import com.laborplanner.backend.service.interfaces.IMachineStatusReadService;
import com.laborplanner.backend.service.interfaces.IMachineTypeReadService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class MachineService implements IMachineService {

  private final MachineRepository machineRepository;
  private final IMachineTypeReadService machineTypeService;
  private final IMachineStatusReadService machineStatusService;

  public MachineService(
      MachineRepository machineRepository,
      IMachineTypeReadService machineTypeService,
      IMachineStatusReadService machineStatusService) {
    this.machineRepository = machineRepository;
    this.machineTypeService = machineTypeService;
    this.machineStatusService = machineStatusService;
  }

  // ---------------------------
  // IMachineService Implementation
  // ---------------------------

  @Override
  public List<MachineDto> getAllMachines() {
    return machineRepository.findAll().stream().map(this::toDto).toList();
  }

  @Override
  public MachineDto getMachineByUuid(String uuid) {
    Machine machine =
        machineRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("Machine not found: {}", uuid);
                  return new MachineNotFoundException(uuid);
                });

    return toDto(machine);
  }

  @Override
  public MachineDto createMachine(MachineDto machine) {
    log.info(
        "Creating machine: name='{}', type='{}', status='{}'",
        machine.getName(),
        machine.getMachineTypeUuid(),
        machine.getMachineStatusUuid());

    // Check if machineTypes Exist
    if (machineTypeService.getAllTypes().isEmpty()) {
      throw new NoMachineTypesExistException();
    }

    // Resolve type and status
    MachineType type = machineTypeService.getTypeByUuid(machine.getMachineTypeUuid());
    MachineStatus status = machineStatusService.getStatusByUuid(machine.getMachineStatusUuid());

    // Convert from Dto -> Model (extracted to small helper to keep mapping in one place)
    Machine model = toModel(machine, type, status);

    if (machineRepository.existsByName(machine.getName())) {
      log.warn("Duplicate machine name attempted: {}", machine.getName());
      throw new DuplicateMachineNameException(machine.getName());
    }
    MachineDto created = toDto(machineRepository.create(model));
    log.info("Machine created successfully: uuid='{}'", created.getMachineUuid());
    return created;
  }

  @Override
  public MachineDto updateMachine(String uuid, MachineDto updatedMachine) {
    Machine existing =
        machineRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("Machine not found for update: uuid='{}'", uuid);
                  return new MachineNotFoundException(uuid);
                });

    // Resolve type and status
    MachineType type = machineTypeService.getTypeByUuid(updatedMachine.getMachineTypeUuid());
    MachineStatus status =
        machineStatusService.getStatusByUuid(updatedMachine.getMachineStatusUuid());

    // If name changed, ensure uniqueness
    if (!existing.getName().equals(updatedMachine.getName())
        && machineRepository.existsByName(updatedMachine.getName())) {
      log.warn("Duplicate machine name attempted during update: {}", updatedMachine.getName());
      throw new DuplicateMachineNameException(updatedMachine.getName());
    }

    existing.setName(updatedMachine.getName());
    existing.setDescription(updatedMachine.getDescription());
    existing.setType(type);
    existing.setStatus(status);

    MachineDto saved = toDto(machineRepository.update(existing));
    log.info("Machine updated successfully: uuid='{}'", saved.getMachineUuid());

    return saved;
  }

  // TODO: Add check so that it cna't delete machine type if machines with that machine type exists
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
  public List<MachineDto> findByType(MachineType type) {
    return machineRepository.findByType(type).stream().map(this::toDto).toList();
  }

  // NOTE: To be implemented in repo
  @Override
  public List<MachineDto> findByStatus(MachineStatus status) {
    return machineRepository.findByStatus(status).stream().map(this::toDto).toList();
  }

  // NOTE: To be implemented in repo
  @Override
  public List<MachineDto> findByTypeAndStatus(MachineType type, MachineStatus status) {
    return machineRepository.findByTypeAndStatus(type, status).stream().map(this::toDto).toList();
  }

  // ---------------------------
  // Private mapping helper(s)
  // ---------------------------

  // Keeps DTO -> Model mapping centralized and mirrors the flow you used in createMachine.
  private Machine toModel(MachineDto dto, MachineType type, MachineStatus status) {
    return new Machine(dto.getName(), dto.getDescription(), type, status);
  }

  private MachineDto toDto(Machine model) {
    MachineType type = model.getType();
    MachineStatus status = model.getStatus();
    return new MachineDto(
        model.getMachineUuid(),
        model.getName(),
        model.getDescription(),
        type.getMachineTypeUuid(),
        status.getMachineStatusUuid());
  }
}
