package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.repository.entity.ScheduleEntity;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;

@Mapper(componentModel = "spring", uses = { UserMapper.class,
      ScheduledJobMapper.class }, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ScheduleMapper extends BaseMapper<Schedule, ScheduleEntity> {

   // Context-aware mapping: pass the weekStart LocalDate so ScheduledJobMapper can
   // rehydrate TimeGrain.fromIndex(...)
   @Mapping(source = "createdByUser", target = "createdByUser")
   @Mapping(source = "scheduledJobList", target = "scheduledJobList", qualifiedByName = "toModelWithContext")
   Schedule toModel(ScheduleEntity entity, @Context LocalDate weekStartDate);

   // Backwards-compatible default method: when callers use toModel(entity) without
   // a context, delegate to the context-aware method using the entity's
   // weekStartDate.
   default Schedule toModel(ScheduleEntity entity) {
      if (entity == null) {
         return null;
      }
      LocalDate weekStart = entity.getWeekStartDate() != null ? entity.getWeekStartDate().toLocalDate() : null;
      return toModel(entity, weekStart);
   }

   // Domain -> Entity
   @Mapping(source = "createdByUser", target = "createdByUser")
   @Mapping(source = "scheduledJobList", target = "scheduledJobList")
   ScheduleEntity toEntity(Schedule model);

   @AfterMapping
   default void linkChildren(
         Schedule model,
         @MappingTarget ScheduleEntity entity) {
      if (entity.getScheduledJobList() != null) {
         entity.getScheduledJobList()
               .forEach(job -> job.setSchedule(entity));
      }
   }
}
