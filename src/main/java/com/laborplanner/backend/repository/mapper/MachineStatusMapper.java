package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.repository.entity.MachineStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MachineStatusMapper extends BaseMapper<MachineStatus, MachineStatusEntity> {

  MachineStatusMapper INSTANCE = Mappers.getMapper(MachineStatusMapper.class);

  MachineStatus toModel(MachineStatusEntity entity);

  MachineStatusEntity toEntity(MachineStatus model);
}
