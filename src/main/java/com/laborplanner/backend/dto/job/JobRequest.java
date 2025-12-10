package com.laborplanner.backend.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
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
    * Prefer integer minutes when coming from the UI. The frontend can send
    * durationMinutes
    */
   @Schema(type = "string", example = "PT1H30M", description = "ISO-8601 duration format (e.g. PT45M, PT2H, PT1H30M). Only used as placeholder for parsing duration to Duration.")
   private String duration;

   /**
    * Number of minutes for the duration. Frontend AddJobModal already works with
    * integer minutes;
    * prefer sending this as it avoids any string formatting edge cases.
    */
   @Schema(type = "integer", example = "90", description = "Duration in whole minutes. Prefer this field from the UI.")
   private Integer durationMinutes;

   @NotNull
   private LocalDateTime deadline;

   @NotBlank
   private String requiredMachineTypeUuid;

   public Duration getDurationAsDuration() {
      if (durationMinutes != null) {
         return Duration.ofMinutes(durationMinutes.longValue());
      }
      // fallback for older clients that send ISO-8601 strings
      return Duration.parse(duration);
   }
}
