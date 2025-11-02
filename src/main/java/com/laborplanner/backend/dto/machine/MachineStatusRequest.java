package com.laborplanner.backend.dto.machine;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MachineStatusRequest {
  @NotBlank private String name;
}
