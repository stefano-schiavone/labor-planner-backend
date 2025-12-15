package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.repository.entity.JobEntity;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { JobTemplateMapper.class, MachineTypeMapper.class })
public interface JobMapper extends BaseMapper<Job, JobEntity> {

   @Mapping(source = "duration", target = "durationMinutes")
   @Mapping(source = "template", target = "template")
   @Mapping(source = "requiredMachineType", target = "requiredMachineType")
   @Mapping(source = "dependencies", target = "dependencies")
   Job toModel(JobEntity entity);

   @Mapping(source = "durationMinutes", target = "duration")
   @Mapping(source = "template", target = "template")
   @Mapping(source = "requiredMachineType", target = "requiredMachineType")
   @Mapping(source = "dependencies", target = "dependencies")
   JobEntity toEntity(Job model);

   List<Job> toModels(List<JobEntity> entities);

   List<JobEntity> toEntities(List<Job> models);

}
