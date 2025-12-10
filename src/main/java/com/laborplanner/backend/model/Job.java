package com.laborplanner.backend.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

   // Fields
   private String jobUuid;

   private JobTemplate template;

   private String name;

   private String description;

   @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "MINUTES")
   private Duration duration;

   private LocalDateTime deadline;

   private MachineType requiredMachineType;

   // UUID only for JSON deserialization
   private String requiredMachineTypeUuid;

   // Self-referencing many-to-many for dependencies
   private List<Job> dependencies = new ArrayList<>();

   // Constructors
   // Constructor with all arguments except UUID
   public Job(
         String name,
         String description,
         Duration duration,
         LocalDateTime deadline,
         MachineType requiredMachineType,
         JobTemplate template) {
      this.name = name;
      this.description = description;
      this.duration = duration;
      this.deadline = deadline;
      this.requiredMachineType = requiredMachineType;
      this.requiredMachineTypeUuid = requiredMachineType != null ? requiredMachineType.getMachineTypeUuid() : null;
      this.template = template;
   }

   // Delegating constructor to allow Jackson to deserialize a single string as a
   // Job UUID.
   // Example JSON => dependencies: ["65eae7b1-..."]
   @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
   public Job(String jobUuid) {
      this.jobUuid = jobUuid;
   }

   // Dependency helper
   public void addDependency(Job dependency) {
      this.dependencies.add(dependency);
   }
}
