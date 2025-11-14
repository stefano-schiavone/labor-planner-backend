package com.laborplanner.backend.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequest {

  private String templateUuid;

  @NotBlank private String name;

  private String description;

  @NotNull private Duration duration;

  @NotNull private LocalDateTime deadline;

  @NotBlank private String requiredMachineTypeUuid;
}
