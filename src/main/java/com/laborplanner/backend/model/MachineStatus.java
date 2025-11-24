package com.laborplanner.backend.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class MachineStatus {

   // Fields
   private String machineStatusUuid;

   private String name;

   // Constructors
   // Constructor with all arguments except UUID
   public MachineStatus(String name) {
      this.name = name;
   }
}
