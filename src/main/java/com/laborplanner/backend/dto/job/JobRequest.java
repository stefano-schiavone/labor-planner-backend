package com.laborplanner.backend.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequest {

   private String templateUuid;

   @NotBlank
   private String name;

   @NotBlank
   private String description;

   /**
    * Number of minutes for the duration. Frontend AddJobModal works with integer
    * minutes;
    * prefer sending this as it avoids any string formatting edge cases.
    */
   @Schema(type = "integer", example = "90", description = "Duration in whole minutes. Required.")
   @NotNull
   private Integer durationMinutes;

   @NotNull
   private LocalDateTime deadline;

   @NotBlank
   private String requiredMachineTypeUuid;
}
