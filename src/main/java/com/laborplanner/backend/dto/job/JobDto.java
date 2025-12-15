package com.laborplanner.backend.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JobDto {

   private String jobUuid;

   private String templateUuid;

   @NotBlank
   private String name;

   @NotBlank
   private String description;

   /**
    * Duration in whole minutes. Domain/service code will use minutes everywhere.
    */
   @NotNull
   private Integer durationMinutes;

   @NotNull
   private LocalDateTime deadline;

   @NotBlank
   private String requiredMachineTypeUuid;

   public JobDto(
         String templateUuid,
         String name,
         String description,
         Integer durationMinutes,
         LocalDateTime deadline,
         String requiredMachineTypeUuid) {
      this.templateUuid = templateUuid;
      this.name = name;
      this.description = description;
      this.durationMinutes = durationMinutes;
      this.deadline = deadline;
      this.requiredMachineTypeUuid = requiredMachineTypeUuid;
   }
}
