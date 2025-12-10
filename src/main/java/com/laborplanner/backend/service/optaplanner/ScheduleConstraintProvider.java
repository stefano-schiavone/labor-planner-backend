package com.laborplanner.backend.service.optaplanner;

import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.model.TimeGrain;
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
            machineTypeMismatch(factory)
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
                     int startIndex = job.getStartingTimeGrain().getGrainIndex();
                     int minIndex = ScheduledJob.START_HOUR
                           * 60
                           / TimeGrain.GRAIN_LENGTH_IN_MINUTES; // assuming 15-min grains
                     int maxIndex = ScheduledJob.END_HOUR * 60 / TimeGrain.GRAIN_LENGTH_IN_MINUTES;
                     return startIndex < minIndex || startIndex >= maxIndex;
                  })
            .penalize("Job outside allowed hours", HardSoftScore.ONE_HARD);
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
}
