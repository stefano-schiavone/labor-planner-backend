package com.laborplanner.backend.mapper;

import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.entity.MachineTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MachineTypeMapper {

  MachineTypeMapper INSTANCE = Mappers.getMapper(MachineTypeMapper.class);

  MachineType toModel(MachineTypeEntity entity);

  MachineTypeEntity toEntity(MachineType model);
}
