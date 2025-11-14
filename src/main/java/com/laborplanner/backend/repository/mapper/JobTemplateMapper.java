package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.repository.entity.JobTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {MachineTypeMapper.class, UserMapper.class})
public interface JobTemplateMapper extends BaseMapper<JobTemplate, JobTemplateEntity> {

  @Mapping(source = "requiredMachineType", target = "requiredMachineType")
  @Mapping(source = "createdByUser", target = "createdByUser")
  JobTemplate toModel(JobTemplateEntity entity);

  @Mapping(source = "requiredMachineType", target = "requiredMachineType")
  @Mapping(source = "createdByUser", target = "createdByUser")
  JobTemplateEntity toEntity(JobTemplate model);
}
