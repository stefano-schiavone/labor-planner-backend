package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.repository.entity.MachineStatusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MachineStatusMapper extends BaseMapper<MachineStatus, MachineStatusEntity> {

  MachineStatus toModel(MachineStatusEntity entity);

  MachineStatusEntity toEntity(MachineStatus model);
}
