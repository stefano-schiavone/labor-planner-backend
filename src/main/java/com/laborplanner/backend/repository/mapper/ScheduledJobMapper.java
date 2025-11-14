package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.model.TimeGrain;
import com.laborplanner.backend.repository.entity.ScheduledJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    uses = {JobMapper.class, MachineMapper.class})
public interface ScheduledJobMapper extends BaseMapper<ScheduledJob, ScheduledJobEntity> {

  @Mapping(source = "job", target = "job")
  @Mapping(source = "machine", target = "machine")
  @Mapping(
      source = "startingTimeGrainIndex",
      target = "startingTimeGrain",
      qualifiedByName = "toTimeGrain")
  ScheduledJob toModel(ScheduledJobEntity entity);

  @Mapping(source = "job", target = "job")
  @Mapping(source = "machine", target = "machine")
  @Mapping(
      source = "startingTimeGrain",
      target = "startingTimeGrainIndex",
      qualifiedByName = "toGrainIndex")
  @Mapping(target = "schedule", ignore = true)
  ScheduledJobEntity toEntity(ScheduledJob model);

  // Convert TimeGrain -> int (DB)
  @Named("toGrainIndex")
  default int toGrainIndex(TimeGrain tg) {
    return tg.getGrainIndex();
  }

  // Convert int (DB) -> TimeGrain
  @Named("toTimeGrain")
  default TimeGrain toTimeGrain(int grainIndex) {
    TimeGrain tg = new TimeGrain();
    tg.setGrainIndex(grainIndex);
    tg.setStartingMinuteOfDay(grainIndex * TimeGrain.GRAIN_LENGTH_IN_MINUTES);
    return tg;
  }
}
