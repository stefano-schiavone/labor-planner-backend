package com.laborplanner.backend.model;

import lombok.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity // For Optaplanner
public class ScheduledJob {

  // Fields
  private String scheduledJobUuid;

  private Job job;

  private Machine machine;

  @PlanningVariable(valueRangeProviderRefs = "timeGrainRange")
  private TimeGrain startingTimeGrain;

  // Constants for allowed scheduling window
  public static final int START_HOUR = 7;
  public static final int END_HOUR = 18;

  // Constructor
  public ScheduledJob(Job job, Machine machine, TimeGrain startingTimeGrain) {
    this.job = job;
    this.machine = machine;
    validateTimeGrain(startingTimeGrain);
    this.startingTimeGrain = startingTimeGrain;
  }

  private void validateTimeGrain(TimeGrain tg) {
    int startGrainIndex = START_HOUR * 60 / TimeGrain.GRAIN_LENGTH_IN_MINUTES;
    int endGrainIndex = END_HOUR * 60 / TimeGrain.GRAIN_LENGTH_IN_MINUTES;

    if (tg.getGrainIndex() < startGrainIndex || tg.getGrainIndex() >= endGrainIndex) {
      throw new IllegalArgumentException(
          String.format(
              "Scheduled job must start between %02d:00 and %02d:00", START_HOUR, END_HOUR));
    }
  }
}

// @PlanningVariable(valueRangeProviderRefs = "availableTimeGrains")
// private TimeGrain startingTimeGrain;

// private Constraint jobOutsideAllowedWindow(ConstraintFactory factory) {
//     int startGrainIndex = 7 * 60 / TimeGrain.GRAIN_LENGTH_IN_MINUTES; // 84
//     int endGrainIndex = 18 * 60 / TimeGrain.GRAIN_LENGTH_IN_MINUTES; // 216

//     return factory.forEach(ScheduledJob.class)
//             .filter(job -> job.getStartingTimeGrain().getGrainIndex() < startGrainIndex
//                         || job.getStartingTimeGrain().getGrainIndex() >= endGrainIndex)
//             .penalize(HardSoftScore.ONE_HARD)
//             .asConstraint("Job outside allowed hours");
// }
