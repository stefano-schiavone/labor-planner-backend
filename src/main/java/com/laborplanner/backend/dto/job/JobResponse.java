package com.laborplanner.backend.dto.job;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JobResponse {
   private String jobUuid;

   private String templateUuid;

   @NotBlank
   private String name;

   @NotBlank
   private String description;

   // expose minutes as integer for the API/UI
   private Integer durationMinutes;

   private LocalDateTime deadline;

   @NotBlank
   private String requiredMachineTypeUuid;
}
