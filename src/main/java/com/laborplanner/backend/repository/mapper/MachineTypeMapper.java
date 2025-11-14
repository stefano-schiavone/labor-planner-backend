package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.entity.MachineTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MachineTypeMapper extends BaseMapper<MachineType, MachineTypeEntity> {

  MachineType toModel(MachineTypeEntity entity);

  MachineTypeEntity toEntity(MachineType model);
}
