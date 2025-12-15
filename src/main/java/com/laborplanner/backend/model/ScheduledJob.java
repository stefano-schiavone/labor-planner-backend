package com.laborplanner.backend.model;

import lombok.*;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity // For Optaplanner
public class ScheduledJob {

   // Fields
   @PlanningId
   private String scheduledJobUuid;

   private Job job;

   @PlanningVariable(valueRangeProviderRefs = { "compatibleMachineRange" })
   private Machine machine;

   @PlanningVariable(valueRangeProviderRefs = "timeGrainRange")
   private TimeGrain startingTimeGrain;

   // Constants for allowed scheduling window
   public static final int START_HOUR = 7;
   public static final int END_HOUR = 18;

   // Runtime-only field for storing compatible machines
   private List<Machine> compatibleMachines;

   // Constructor
   public ScheduledJob(Job job, Machine machine, TimeGrain startingTimeGrain) {
      this.job = job;
      this.machine = machine;
      this.startingTimeGrain = startingTimeGrain;
   }

   public void setCompatibleMachines(List<Machine> machines) {
      this.compatibleMachines = machines;
   }

   // Helper: compute how many time grains this job occupies (round up)
   public int getDurationInGrains() {
      if (job == null || job.getDurationMinutes() == null) {
         return 1;
      }
      long minutes = job.getDurationMinutes();
      int grainSize = TimeGrain.GRAIN_LENGTH_IN_MINUTES;
      int grains = (int) ((minutes + grainSize - 1) / grainSize);
      return Math.max(1, grains);
   }

   // Helper: last (exclusive) grain index (end = start + durationInGrains)
   public Integer getEndTimeGrainIndex() {
      if (startingTimeGrain == null) {
         return null;
      }
      return startingTimeGrain.getGrainIndex() + getDurationInGrains(); // exclusive end
   }

   // Overlap check similar to MeetingAssignment.calculateOverlap
   public boolean overlapsWith(ScheduledJob other) {
      if (this.startingTimeGrain == null || other == null || other.getStartingTimeGrain() == null) {
         return false;
      }
      int thisStart = this.startingTimeGrain.getGrainIndex();
      int otherStart = other.getStartingTimeGrain().getGrainIndex();
      int thisEnd = thisStart + this.getDurationInGrains(); // exclusive
      int otherEnd = otherStart + other.getDurationInGrains(); // exclusive
      // intervals [start, end) overlap when start < otherEnd && otherStart < end
      return thisStart < otherEnd && otherStart < thisEnd;
   }
}
