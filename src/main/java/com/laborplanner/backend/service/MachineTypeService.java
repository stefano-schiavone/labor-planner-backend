package com.laborplanner.backend.service;

import com.laborplanner.backend.exception.machine.DuplicateMachineTypeNameException;
import com.laborplanner.backend.exception.machine.MachineTypeNotFoundException;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.MachineTypeRepository;
import com.laborplanner.backend.service.interfaces.IMachineTypeService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MachineTypeService implements IMachineTypeService {

  private final MachineTypeRepository machineTypeRepository;

  public MachineTypeService(MachineTypeRepository machineTypeRepository) {
    this.machineTypeRepository = machineTypeRepository;
  }

  @Override
  public List<MachineType> getAllTypes() {
    return machineTypeRepository.findAllByOrderByNameAsc();
  }

  @Override
  public MachineType getTypeByUuid(String uuid) {
    return machineTypeRepository
        .findByUuid(uuid)
        .orElseThrow(
            () -> {
              log.warn("Machine type not found: {}", uuid);
              return new MachineTypeNotFoundException(uuid);
            });
  }

  @Override
  public MachineType createType(MachineType type) {
    log.info("Creating machine type: name='{}'", type.getName());

    if (machineTypeRepository.existsByName(type.getName())) {
      log.warn("Duplicate machine type name attempted: {}", type.getName());
      throw new DuplicateMachineTypeNameException(type.getName());
    }

    MachineType created = machineTypeRepository.create(type);
    log.info("Machine type created successfully: uuid='{}'", created.getMachineTypeUuid());
    return created;
  }

  @Override
  public MachineType updateType(String uuid, MachineType updatedType) {
    MachineType existing =
        machineTypeRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("Machine type not found for update: uuid='{}'", uuid);
                  return new MachineTypeNotFoundException(uuid);
                });

    existing.setName(updatedType.getName());
    MachineType saved = machineTypeRepository.update(existing);
    log.info("Machine type updated successfully: uuid='{}'", saved.getMachineTypeUuid());
    return saved;
  }

  @Override
  public void deleteType(String uuid) {
    if (!machineTypeRepository.existsByUuid(uuid)) {
      throw new MachineTypeNotFoundException(uuid);
    }
    machineTypeRepository.deleteByUuid(uuid);
    log.info("Deleted machine type: uuid='{}'", uuid);
  }

  @Override
  public Optional<MachineType> findByName(String name) {
    return machineTypeRepository.findByName(name);
  }
}
