package com.laborplanner.backend.model;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class JobTemplate {

   // Fields
   private String jobTemplateUuid;

   private String name;

   private String description;

   @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "MINUTES")
   private Duration duration;

   private MachineType requiredMachineType;

   private User createdByUser;

   // Constructors
   // Constructor with all arguments except UUID
   public JobTemplate(
         String name,
         String description,
         Duration duration,
         MachineType requiredMachineType,
         User createdByUser) {
      this.name = name;
      this.description = description;
      this.duration = duration;
      this.requiredMachineType = requiredMachineType;
      this.createdByUser = createdByUser;
   }
}
