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
            preferEarlyFinishOverall(factory),
            preferOneGrainGapBetweenJobs(factory),
            penalizeLargeGapBetweenJobs(factory),
            penalizeUnnecessaryLateStart(factory)
      };
   }

   Constraint jobWithinAllowedHours(ConstraintFactory factory) {
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

   Constraint jobMustFinishWithinDay(ConstraintFactory factory) {
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

   Constraint machineTypeMismatch(ConstraintFactory factory) {
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

   Constraint machineConflict(ConstraintFactory factory) {
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

   Constraint jobMustFinishBeforeDeadline(ConstraintFactory factory) {
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

   Constraint preferEarlyFinishOverall(ConstraintFactory factory) {
      return factory.forEach(ScheduledJob.class)
            .penalize(
                  "Prefer early finish (week > day)",
                  HardSoftScore.ONE_SOFT,
                  sj -> {
                     if (sj.getStartingTimeGrain() == null) {
                        return 0;
                     }
                     // Later grainIndex = higher penalty
                     return sj.getStartingTimeGrain().getGrainIndex();
                  });
   }

   Constraint preferOneGrainGapBetweenJobs(ConstraintFactory factory) {
      return factory.forEachUniquePair(
            ScheduledJob.class,
            Joiners.equal(ScheduledJob::getMachine))
            .filter((sj1, sj2) -> {
               if (sj1.getStartingTimeGrain() == null || sj2.getStartingTimeGrain() == null) {
                  return false;
               }
               // Ignore overlapping jobs (handled by hard constraint)
               return !sj1.overlapsWith(sj2);
            })
            .penalize(
                  "Prefer 1-grain gap between jobs",
                  HardSoftScore.ONE_HARD,
                  (sj1, sj2) -> {
                     int gap = gapInGrains(sj1, sj2);

                     // Penalize if gap < 1
                     return gap >= 1 ? 0 : (1 - gap);
                  });
   }

   // HELPERS
   private int gapInGrains(ScheduledJob a, ScheduledJob b) {
      int aStart = a.getStartingTimeGrain().getGrainIndex();
      int aEnd = aStart + a.getDurationInGrains();

      int bStart = b.getStartingTimeGrain().getGrainIndex();
      int bEnd = bStart + b.getDurationInGrains();

      // Determine order
      if (aEnd <= bStart) {
         return bStart - aEnd;
      } else if (bEnd <= aStart) {
         return aStart - bEnd;
      } else {
         // overlapping
         return 0;
      }
   }

   Constraint penalizeLargeGapBetweenJobs(ConstraintFactory factory) {
      return factory.forEachUniquePair(
            ScheduledJob.class,
            Joiners.equal(ScheduledJob::getMachine))
            .filter((sj1, sj2) -> {
               if (sj1.getStartingTimeGrain() == null || sj2.getStartingTimeGrain() == null) {
                  return false;
               }
               // Only consider jobs on the same day
               if (!sj1.getStartingTimeGrain().getDate().equals(sj2.getStartingTimeGrain().getDate())) {
                  return false;
               }
               // Ignore overlapping jobs (handled by hard constraint)
               if (sj1.overlapsWith(sj2)) {
                  return false;
               }
               // Check if gap is greater than 1 grain
               int gap = gapInGrains(sj1, sj2);
               return gap > 1;
            })
            .penalize(
                  "Penalize large gap between jobs on same day",
                  HardSoftScore.ONE_SOFT,
                  (sj1, sj2) -> {
                     int gap = gapInGrains(sj1, sj2);
                     // Penalize the excess gap beyond 1 grain
                     return gap - 1;
                  });
   }

   Constraint penalizeUnnecessaryLateStart(ConstraintFactory factory) {
      return factory.forEach(ScheduledJob.class)
            .filter(sj -> sj.getStartingTimeGrain() != null && sj.getMachine() != null)
            .ifNotExists(
                  ScheduledJob.class,
                  Joiners.equal(ScheduledJob::getMachine),
                  Joiners.filtering((sj, other) -> {
                     if (other.getStartingTimeGrain() == null) {
                        return false;
                     }
                     // Check if 'other' is on the same day and ends before 'sj' starts
                     if (!sj.getStartingTimeGrain().getDate().equals(other.getStartingTimeGrain().getDate())) {
                        return false;
                     }
                     // 'other' must end before or at 'sj' start (i.e., other is a predecessor)
                     int otherEnd = other.getStartingTimeGrain().getGrainIndex() + other.getDurationInGrains();
                     int sjStart = sj.getStartingTimeGrain().getGrainIndex();
                     return otherEnd <= sjStart && !sj.equals(other);
                  }))
            .penalize(
                  "Penalize unnecessary late start",
                  HardSoftScore.ONE_SOFT,
                  sj -> {
                     // Penalize based on how far from the start of the day this job begins
                     int grainsPerDay = (ScheduledJob.END_HOUR - ScheduledJob.START_HOUR) * 60
                           / TimeGrain.GRAIN_LENGTH_IN_MINUTES;
                     int dayStartIndex = (sj.getStartingTimeGrain().getGrainIndex() / grainsPerDay) * grainsPerDay;
                     int grainOffset = sj.getStartingTimeGrain().getGrainIndex() - dayStartIndex;
                     return grainOffset;
                  });
   }
}
