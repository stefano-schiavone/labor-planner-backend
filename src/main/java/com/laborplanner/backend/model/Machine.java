package com.laborplanner.backend.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

   // Fields
   private String machineUuid;

   private String name;

   private String description;

   private MachineType type;

   private MachineStatus status;

   // Constructors
   // Constructor with all arguments except UUID
   public Machine(String name, String description, MachineType type, MachineStatus status) {
      this.name = name;
      this.description = description;
      this.type = type;
      this.status = status;
   }

   @Override
   public int hashCode() {
      return machineUuid != null ? machineUuid.hashCode() : 0;
   }

   @Override
   public String toString() {
      return name != null ? name : machineUuid;
   }
}
