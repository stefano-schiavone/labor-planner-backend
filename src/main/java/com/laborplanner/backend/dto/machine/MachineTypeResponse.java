package com.laborplanner.backend.dto.machine;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MachineTypeResponse {
  private String machineTypeUuid;
  private String name;
}
