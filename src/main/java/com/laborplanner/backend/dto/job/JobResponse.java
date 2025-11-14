package com.laborplanner.backend.dto.job;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
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

  @NotBlank private String name;

  @NotBlank private String description;

  @NotBlank private Duration duration;

  @NotBlank private LocalDateTime deadline;

  @NotBlank private String requiredMachineTypeUuid;
}
