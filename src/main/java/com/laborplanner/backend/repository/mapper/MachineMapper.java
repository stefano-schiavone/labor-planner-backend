package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.repository.entity.MachineEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {MachineTypeMapper.class, MachineStatusMapper.class})
public interface MachineMapper extends BaseMapper<Machine, MachineEntity> {

  @Mapping(source = "type", target = "type")
  @Mapping(source = "status", target = "status")
  Machine toModel(MachineEntity entity);

  @Mapping(source = "type", target = "type")
  @Mapping(source = "status", target = "status")
  MachineEntity toEntity(Machine model);
}
