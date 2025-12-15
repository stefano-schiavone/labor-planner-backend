package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.model.TimeGrain;
import com.laborplanner.backend.repository.entity.ScheduleEntity;
import com.laborplanner.backend.repository.entity.ScheduledJobEntity;

import java.time.LocalDate;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { JobMapper.class,
      MachineMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduledJobMapper extends BaseMapper<ScheduledJob, ScheduledJobEntity> {

   @Mapping(source = "job", target = "job")
   @Mapping(source = "machine", target = "machine")
   @Mapping(target = "startingTimeGrain", ignore = true)
   // MapStruct will call this and we rely on the @Context weekStartDate to
   // rehydrate the TimeGrain
   @Named("toModelWithContext")
   ScheduledJob toModel(ScheduledJobEntity entity, @Context LocalDate weekStartDate);

   @Mapping(source = "job", target = "job")
   @Mapping(source = "machine", target = "machine")
   @Mapping(source = "startingTimeGrain", target = "startingTimeGrainIndex", qualifiedByName = "toGrainIndex")
   @Mapping(target = "schedule", ignore = true)
   ScheduledJobEntity toEntity(ScheduledJob model);

   // Convert TimeGrain -> int (DB)
   @Named("toGrainIndex")
   default int toGrainIndex(TimeGrain tg) {
      return tg.getGrainIndex();
   }

   @AfterMapping
   default void rehydrateGrain(
         ScheduledJobEntity entity,
         @MappingTarget ScheduledJob model,
         @Context LocalDate weekStartDate) {
      // Only rehydrate if the DB value is meaningful and weekStartDate is available
      model.setStartingTimeGrain(
            TimeGrain.fromIndex(entity.getStartingTimeGrainIndex(), weekStartDate));
   }

   @AfterMapping
   default void linkSchedule(
         ScheduledJob model,
         @MappingTarget ScheduledJobEntity entity,
         @Context ScheduleEntity schedule) {

      entity.setSchedule(schedule);
   }

}
