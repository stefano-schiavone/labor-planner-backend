package com.laborplanner.backend.dto.scheduledJob;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.TimeGrain;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScheduledJobDto {

   @NotBlank
   private String scheduledJobUuid;

   private Job job;

   private Machine assignedMachine;

   private TimeGrain assignedTimeGrain;

   public ScheduledJobDto(Job job, Machine assignedMachine, TimeGrain assignedTimeGrain) {
      this.job = job;
      this.assignedMachine = assignedMachine;
      this.assignedTimeGrain = assignedTimeGrain;
   }
}
