package com.laborplanner.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.*;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@Getter
@Setter
@NoArgsConstructor
@PlanningSolution // Optaplanner
public class Schedule {

   // Fields
   private String scheduleUuid;

   private LocalDateTime weekStartDate;

   private User createdByUser;

   private LocalDateTime lastModifiedDate;

   // Planning fields
   @ProblemFactCollectionProperty
   private List<Job> jobList;

   @ProblemFactCollectionProperty
   private List<Machine> machineList;

   @ValueRangeProvider(id = "timeGrainRange")
   @ProblemFactCollectionProperty
   private List<TimeGrain> timeGrainList;

   @PlanningEntityCollectionProperty
   private List<ScheduledJob> scheduledJobList;

   @PlanningScore
   private HardSoftScore score;

   // Constructors
   // Constructor with all arguments except UUID
   public Schedule(
         LocalDateTime weekStartDate,
         User createdByUser,
         LocalDateTime lastModifiedDate,
         List<Machine> machineList,
         List<TimeGrain> timeGrainList,
         List<Job> jobList,
         List<ScheduledJob> scheduledJobList) {
      this.weekStartDate = weekStartDate;
      this.createdByUser = createdByUser;
      this.lastModifiedDate = lastModifiedDate;
      this.machineList = machineList;
      this.timeGrainList = timeGrainList;
      this.jobList = jobList;
      this.scheduledJobList = scheduledJobList;
   }

   public static List<TimeGrain> generateTimeGrains(LocalDate weekStartDate) {
      List<TimeGrain> grains = new ArrayList<>();
      int grainIndex = 0;

      for (int day = 0; day < 7; day++) {
         LocalDate date = weekStartDate.plusDays(day);

         int startMinute = ScheduledJob.START_HOUR * 60;
         int endMinute = ScheduledJob.END_HOUR * 60;

         for (int minute = startMinute; minute < endMinute; minute += TimeGrain.GRAIN_LENGTH_IN_MINUTES) {
            grains.add(new TimeGrain(
                  grainIndex++,
                  minute,
                  date));
         }
      }

      return grains;
   }

   // Provides the compatible machines for a specific requiredMachineType for a job
   // This value is then set as the planning variable on top of machine
   @ValueRangeProvider(id = "compatibleMachineRange")
   @ProblemFactCollectionProperty
   public List<Machine> getCompatibleMachinesForSolver() {
      LinkedHashSet<Machine> unique = new LinkedHashSet<>();
      if (scheduledJobList == null) {
         return new ArrayList<>(unique);
      }
      for (ScheduledJob sj : scheduledJobList) {
         if (sj.getCompatibleMachines() != null) {
            unique.addAll(sj.getCompatibleMachines());
         }
      }
      return new ArrayList<>(unique);
   }
}
