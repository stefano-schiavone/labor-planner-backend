package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.repository.entity.ScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, ScheduledJobMapper.class},
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ScheduleMapper extends BaseMapper<Schedule, ScheduleEntity> {

  // Entity -> Domain
  @Mapping(source = "createdByUser", target = "createdByUser")
  @Mapping(source = "scheduledJobList", target = "scheduledJobList")
  Schedule toModel(ScheduleEntity entity);

  // Domain -> Entity
  @Mapping(source = "createdByUser", target = "createdByUser")
  @Mapping(source = "scheduledJobList", target = "scheduledJobList")
  ScheduleEntity toEntity(Schedule model);
}
