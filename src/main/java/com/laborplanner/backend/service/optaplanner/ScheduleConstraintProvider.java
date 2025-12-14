package com.laborplanner.backend.service.optaplanner;

import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.model.TimeGrain;

import java.time.LocalDateTime;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class ScheduleConstraintProvider implements ConstraintProvider {

   @Override
   public Constraint[] defineConstraints(ConstraintFactory factory) {
      return new Constraint[] {
            jobWithinAllowedHours(factory),
            machineConflict(factory),
            machineTypeMismatch(factory),
            jobMustFinishBeforeDeadline(factory),
            jobMustFinishWithinDay(factory),
            preferEarlyStart(factory)
      };
   }

   private Constraint jobWithinAllowedHours(ConstraintFactory factory) {
      return factory
            .forEach(ScheduledJob.class)
            .filter(
                  job -> {
                     if (job.getStartingTimeGrain() == null) {
                        return false;
                     }
                     // Use the TimeGrain startingMinuteOfDay (minutes since midnight)
                     int startingMinute = job.getStartingTimeGrain().getStartingMinuteOfDay();
                     int minMinute = ScheduledJob.START_HOUR * 60; // 7 * 60 = 420
                     int maxMinute = ScheduledJob.END_HOUR * 60; // 18 * 60 = 1080
                     return startingMinute < minMinute || startingMinute >= maxMinute;
                  })
            .penalize("Job outside allowed hours", HardSoftScore.ONE_HARD);
   }

   private Constraint jobMustFinishWithinDay(ConstraintFactory factory) {
      return factory.forEach(ScheduledJob.class)
            .filter(sj -> {
               if (sj.getStartingTimeGrain() == null)
                  return false;
               int endIndex = sj.getStartingTimeGrain().getGrainIndex() + sj.getDurationInGrains();
               int grainsPerDay = (ScheduledJob.END_HOUR - ScheduledJob.START_HOUR) * 60
                     / TimeGrain.GRAIN_LENGTH_IN_MINUTES;
               int dayStartIndex = (sj.getStartingTimeGrain().getGrainIndex() / grainsPerDay) * grainsPerDay;
               int dayEndIndex = dayStartIndex + grainsPerDay;
               return endIndex > dayEndIndex;
            })
            .penalize("Job crosses day boundary", HardSoftScore.ONE_HARD);
   }

   private Constraint machineTypeMismatch(ConstraintFactory factory) {
      return factory.forEach(ScheduledJob.class)
            .filter(sj -> {
               if (sj.getMachine() == null || sj.getJob() == null || sj.getJob().getRequiredMachineType() == null) {
                  return false; // not yet initialized or missing requirement
               }
               if (sj.getMachine().getType() == null) {
                  return true; // machine without type is incompatible
               }
               String machineTypeUuid = sj.getMachine().getType().getMachineTypeUuid();
               String requiredUuid = sj.getJob().getRequiredMachineType().getMachineTypeUuid();
               return !machineTypeUuid.equals(requiredUuid);
            })
            .penalize("Machine type mismatch", HardSoftScore.ONE_HARD);
   }

   private Constraint machineConflict(ConstraintFactory factory) {
      return factory.forEachUniquePair(ScheduledJob.class,
            Joiners.equal(ScheduledJob::getMachine))
            .filter((sj1, sj2) -> {
               if (sj1.getMachine() == null || sj2.getMachine() == null) {
                  return false;
               }
               if (sj1.getStartingTimeGrain() == null || sj2.getStartingTimeGrain() == null) {
                  return false;
               }
               return sj1.overlapsWith(sj2);
            })
            .penalize("Machine conflict", HardSoftScore.ONE_HARD);
   }

   private Constraint jobMustFinishBeforeDeadline(ConstraintFactory factory) {
      return factory
            .forEach(ScheduledJob.class)
            .filter(sj -> {
               if (sj.getStartingTimeGrain() == null ||
                     sj.getJob() == null ||
                     sj.getJob().getDeadline() == null) {
                  return false;
               }

               LocalDateTime start = sj.getStartingTimeGrain().toDateTime();
               LocalDateTime end = start.plusMinutes(
                     sj.getDurationInGrains() * TimeGrain.GRAIN_LENGTH_IN_MINUTES);

               return end.isAfter(sj.getJob().getDeadline());
            })
            .penalize("Job finishes after deadline", HardSoftScore.ONE_HARD);
   }

   private Constraint preferEarlyStart(ConstraintFactory factory) {
      int maxMinutes = ScheduledJob.END_HOUR * 60; // e.g., 18*60 = 1080
      return factory.forEach(ScheduledJob.class)
            .reward("Prefer early start", HardSoftScore.ONE_SOFT,
                  sj -> {
                     if (sj.getStartingTimeGrain() == null)
                        return 0;
                     // reward = how much earlier than the latest minute
                     return maxMinutes - sj.getStartingTimeGrain().getStartingMinuteOfDay();
                  });
   }

}
