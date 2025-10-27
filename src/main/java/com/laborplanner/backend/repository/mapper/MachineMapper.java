package com.laborplanner.backend.mapper;

import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.repository.entity.MachineEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    uses = {MachineTypeMapper.class, MachineStatusMapper.class})
public interface MachineMapper {

  MachineMapper INSTANCE = Mappers.getMapper(MachineMapper.class);

  @Mapping(source = "type", target = "type")
  @Mapping(source = "status", target = "status")
  Machine toModel(MachineEntity entity);

  @Mapping(source = "type", target = "type")
  @Mapping(source = "status", target = "status")
  MachineEntity toEntity(Machine model);
}
