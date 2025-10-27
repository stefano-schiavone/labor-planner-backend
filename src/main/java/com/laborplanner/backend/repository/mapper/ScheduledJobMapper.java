package com.laborplanner.backend.mapper;

import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.repository.entity.ScheduledJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    uses = {ScheduleMapper.class, JobMapper.class, MachineMapper.class})
public interface ScheduledJobMapper {

  ScheduledJobMapper INSTANCE = Mappers.getMapper(ScheduledJobMapper.class);

  @Mapping(source = "schedule", target = "schedule")
  @Mapping(source = "job", target = "job")
  @Mapping(source = "machine", target = "machine")
  ScheduledJob toModel(ScheduledJobEntity entity);

  @Mapping(source = "schedule", target = "schedule")
  @Mapping(source = "job", target = "job")
  @Mapping(source = "machine", target = "machine")
  ScheduledJobEntity toEntity(ScheduledJob model);
}
