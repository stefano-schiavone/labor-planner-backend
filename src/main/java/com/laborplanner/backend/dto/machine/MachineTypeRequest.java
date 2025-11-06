package com.laborplanner.backend.dto.machine;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MachineTypeRequest {
  @NotBlank private String name;
}
