package com.laborplanner.backend.service.optaplanner;

import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.model.TimeGrain;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class ScheduleConstraintProvider implements ConstraintProvider {

  @Override
  public Constraint[] defineConstraints(ConstraintFactory factory) {
    return new Constraint[] {jobWithinAllowedHours(factory)};
  }

  private Constraint jobWithinAllowedHours(ConstraintFactory factory) {
    return factory
        .forEach(ScheduledJob.class)
        .filter(
            job -> {
              int startIndex = job.getStartingTimeGrain().getGrainIndex();
              int minIndex =
                  ScheduledJob.START_HOUR
                      * 60
                      / TimeGrain.GRAIN_LENGTH_IN_MINUTES; // assuming 15-min grains
              int maxIndex = ScheduledJob.END_HOUR * 60 / TimeGrain.GRAIN_LENGTH_IN_MINUTES;
              return startIndex < minIndex || startIndex >= maxIndex;
            })
        .penalize("Job outside allowed hours", HardSoftScore.ONE_HARD);
  }
}
