package com.laborplanner.backend.dto.machine;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MachineRequest {

  @NotBlank private String name;

  private String description;

  @NotBlank private String machineTypeUuid;

  @NotBlank private String machineStatusUuid;
}
