package com.laborplanner.backend.dto.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MachineResponse {

  private String machineUuid;
  private String name;
  private String description;
  private String machineTypeUuid;
  private String machineStatusUuid;
}
