package com.laborplanner.backend.dto.machine;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MachineDto {
  private String machineUuid;

  @NotBlank private String name;

  private String description;

  @NotBlank private String machineTypeUuid;

  @NotBlank private String machineStatusUuid;

  public MachineDto(
      String name, String description, String machineTypeUuid, String machineStatusUuid) {
    this.name = name;
    this.description = description;
    this.machineTypeUuid = machineTypeUuid;
    this.machineStatusUuid = machineStatusUuid;
  }
}
