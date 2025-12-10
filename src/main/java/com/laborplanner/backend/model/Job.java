package com.laborplanner.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.*;

/**
 * Domain model for Job. Duration is stored as whole minutes (durationMinutes).
 */
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

   /**
    * Duration in whole minutes.
    * Serializes as a number in JSON (no special string format).
    */
   @JsonAlias({ "duration", "durationMinutes" })
   private Integer durationMinutes;

   private LocalDateTime deadline;

   private MachineType requiredMachineType;

   // UUID only for JSON deserialization
   private String requiredMachineTypeUuid;

   // Self-referencing many-to-many for dependencies
   private List<Job> dependencies = new ArrayList<>();

   // Constructors

   /**
    * Constructor used by service when creating a Job model from DTOs.
    */
   public Job(
         String name,
         String description,
         Integer durationMinutes,
         LocalDateTime deadline,
         MachineType requiredMachineType,
         JobTemplate template) {
      this.name = name;
      this.description = description;
      this.durationMinutes = durationMinutes;
      this.deadline = deadline;
      this.requiredMachineType = requiredMachineType;
      this.requiredMachineTypeUuid = requiredMachineType != null ? requiredMachineType.getMachineTypeUuid() : null;
      this.template = template;
   }

   public Job(
         String uuid,
         String name,
         String description,
         Integer durationMinutes,
         LocalDateTime deadline,
         MachineType requiredMachineType,
         JobTemplate template) {

      this.jobUuid = uuid;
      this.template = template;
      this.name = name;
      this.description = description;
      this.durationMinutes = durationMinutes;
      this.deadline = deadline;
      this.requiredMachineType = requiredMachineType;
      this.requiredMachineTypeUuid = requiredMachineType != null ? requiredMachineType.getMachineTypeUuid() : null;
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
