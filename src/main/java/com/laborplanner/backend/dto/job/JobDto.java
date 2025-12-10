package com.laborplanner.backend.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
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

   @NotNull
   private Duration duration;

   @NotNull
   private LocalDateTime deadline;

   @NotBlank
   private String requiredMachineTypeUuid;

   public JobDto(
         String templateUuid,
         String name,
         String description,
         Duration duration,
         LocalDateTime deadline,
         String requiredMachineTypeUuid) {
      this.templateUuid = templateUuid;
      this.name = name;
      this.description = description;
      this.duration = duration;
      this.deadline = deadline;
      this.requiredMachineTypeUuid = requiredMachineTypeUuid;
   }
}
