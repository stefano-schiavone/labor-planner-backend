package com.laborplanner.backend.dto.schedule;

import com.laborplanner.backend.dto.scheduledJob.ScheduledJobDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleDto {

   private String scheduleUuid;

   @NotNull
   private LocalDateTime weekStartDate;

   private LocalDateTime lastModifiedDate;

   private List<ScheduledJobDto> scheduledJobList;

   public ScheduleDto(LocalDateTime weekStartDate,
         List<ScheduledJobDto> scheduledJobList) {
      this.weekStartDate = weekStartDate;
      this.scheduledJobList = scheduledJobList;
   }
}
