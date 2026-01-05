package com.laborplanner.backend.service.optaplanner;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.model.TimeGrain;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class ScheduleConstraintProviderTest {

   private ConstraintVerifier<ScheduleConstraintProvider, Schedule> verifier;

   @BeforeEach
   void setup() {
      verifier = ConstraintVerifier.build(
            new ScheduleConstraintProvider(),
            Schedule.class,
            ScheduledJob.class);
   }

   @Test
   void jobWithinAllowedHours_whenBeforeStartHour_penalizes() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60 - 1);
      Machine m = machine("m-1", null);

      ScheduledJob sj = scheduledJobInitialized(tg, m, jobWithDurationMinutes(TimeGrain.GRAIN_LENGTH_IN_MINUTES));
      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::jobWithinAllowedHours)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void jobWithinAllowedHours_whenAtOrAfterEndHour_penalizes() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.END_HOUR * 60);
      Machine m = machine("m-1", null);

      ScheduledJob sj = scheduledJobInitialized(tg, m, jobWithDurationMinutes(TimeGrain.GRAIN_LENGTH_IN_MINUTES));
      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::jobWithinAllowedHours)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void preferEarlyFinishOverall_penalizesByGrainIndex() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 7, ScheduledJob.START_HOUR * 60);
      Machine m = machine("m-1", null);

      ScheduledJob sj = scheduledJobInitialized(tg, m, jobWithDurationMinutes(TimeGrain.GRAIN_LENGTH_IN_MINUTES));
      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::preferEarlyFinishOverall)
            .givenSolution(sol)
            .penalizesBy(7);
   }

   @Test
   void jobMustFinishWithinDay_whenCrossesDayBoundary_penalizes() {
      int grainsPerDay = (ScheduledJob.END_HOUR - ScheduledJob.START_HOUR) * 60
            / TimeGrain.GRAIN_LENGTH_IN_MINUTES;

      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), grainsPerDay - 1, ScheduledJob.START_HOUR * 60);
      Machine m = machine("m-1", null);

      Job job = jobWithDurationMinutes(2 * TimeGrain.GRAIN_LENGTH_IN_MINUTES); // 2 grains
      ScheduledJob sj = scheduledJobInitialized(tg, m, job);

      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::jobMustFinishWithinDay)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void jobMustFinishBeforeDeadline_whenEndsAfterDeadline_penalizes() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);
      Machine m = machine("m-1", null);

      Job job = jobWithDurationMinutes(2 * TimeGrain.GRAIN_LENGTH_IN_MINUTES); // end 07:10
      job.setDeadline(LocalDateTime.of(2026, 1, 5, 7, 1)); // before end => penalize

      ScheduledJob sj = scheduledJobInitialized(tg, m, job);
      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::jobMustFinishBeforeDeadline)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void machineTypeMismatch_whenTypeUuidDiff_penalizes() {
      MachineType required = new MachineType();
      required.setMachineTypeUuid("mt-required");

      Job job = jobWithDurationMinutes(TimeGrain.GRAIN_LENGTH_IN_MINUTES);
      job.setRequiredMachineType(required);

      MachineType actual = new MachineType();
      actual.setMachineTypeUuid("mt-actual");

      Machine m = machine("m-1", actual);
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);

      ScheduledJob sj = scheduledJobInitialized(tg, m, job);
      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::machineTypeMismatch)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   // ----------------------------
   // Helpers
   // ----------------------------

   private Schedule solutionWith(ScheduledJob sj, TimeGrain tg, Machine m) {
      Schedule schedule = new Schedule();
      schedule.setScheduleUuid("s-" + UUID.randomUUID());
      schedule.setWeekStartDate(LocalDateTime.of(2026, 1, 5, 0, 0));

      schedule.setScheduledJobList(List.of(sj));
      schedule.setTimeGrainList(List.of(tg));
      schedule.setMachineList(List.of(m));
      schedule.setJobList(sj.getJob() == null ? List.of() : List.of(sj.getJob()));

      return schedule;
   }

   /**
    * Creates a ScheduledJob that is "initialized" from OptaPlanner's perspective:
    * - has PlanningId
    * - has BOTH planning variables set (machine, startingTimeGrain)
    * - machine is within compatibleMachines range (because machine variable uses
    * compatibleMachineRange)
    */
   private ScheduledJob scheduledJobInitialized(TimeGrain tg, Machine machine, Job job) {
      ScheduledJob sj = new ScheduledJob();
      sj.setScheduledJobUuid("sj-" + UUID.randomUUID());
      sj.setJob(job);

      // Planning variables (both must be non-null to avoid init score < 0)
      sj.setStartingTimeGrain(tg);

      sj.setCompatibleMachines(List.of(machine));
      sj.setMachine(machine);

      return sj;
   }

   private Job jobWithDurationMinutes(int minutes) {
      Job job = new Job();
      job.setJobUuid("j-" + UUID.randomUUID());
      job.setDurationMinutes(minutes);
      return job;
   }

   private Machine machine(String uuid, MachineType type) {
      Machine m = new Machine();
      m.setMachineUuid(uuid);
      m.setType(type);
      return m;
   }

   private TimeGrain timeGrain(LocalDate date, int grainIndex, int startingMinuteOfDay) {
      TimeGrain tg = new TimeGrain();
      tg.setDate(date); // REQUIRED for toDateTime()
      tg.setGrainIndex(grainIndex);
      tg.setStartingMinuteOfDay(startingMinuteOfDay);
      return tg;
   }
}
