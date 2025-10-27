package com.laborplanner.backend.mapper;

import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.repository.entity.ScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class})
public interface ScheduleMapper {

  ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

  @Mapping(source = "createdByUser", target = "createdByUser")
  Schedule toModel(ScheduleEntity entity);

  @Mapping(source = "createdByUser", target = "createdByUser")
  ScheduleEntity toEntity(Schedule model);
}
