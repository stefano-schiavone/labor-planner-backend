package com.laborplanner.backend.model;

import java.time.LocalDateTime;
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
  @ProblemFactCollectionProperty private List<Job> jobList;
  @ProblemFactCollectionProperty private List<Machine> machineList;

  @ValueRangeProvider(id = "timeGrainRange")
  @ProblemFactCollectionProperty
  private List<TimeGrain> timeGrainList;

  @PlanningEntityCollectionProperty private List<ScheduledJob> scheduledJobList;

  @PlanningScore private HardSoftScore score;

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
}
