package com.laborplanner.backend.service.optaplanner;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.model.TimeGrain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
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

   @Test
   void machineTypeMismatch_whenMachineNull_returnsNoPenalty() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);
      Machine m = machine("m-1", null);

      Job job = jobWithDurationMinutes(TimeGrain.GRAIN_LENGTH_IN_MINUTES);
      MachineType required = new MachineType();
      required.setMachineTypeUuid("mt-req");
      job.setRequiredMachineType(required);

      ScheduledJob sj = scheduledJobInitialized(tg, m, job);
      sj.setMachine(null); // hit: sj.getMachine() == null -> return false

      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::machineTypeMismatch)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void machineTypeMismatch_whenJobNull_returnsNoPenalty() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);
      Machine m = machine("m-1", null);

      ScheduledJob sj = new ScheduledJob();
      sj.setScheduledJobUuid("sj-" + UUID.randomUUID());
      sj.setStartingTimeGrain(tg);
      sj.setCompatibleMachines(List.of(m));
      sj.setMachine(m);
      sj.setJob(null); // hit: sj.getJob() == null -> return false

      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::machineTypeMismatch)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void machineTypeMismatch_whenRequiredTypeNull_returnsNoPenalty_withoutCallingSetterNull() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);
      Machine m = machine("m-1", null);

      Job job = jobWithDurationMinutes(TimeGrain.GRAIN_LENGTH_IN_MINUTES);
      // Do NOT call setRequiredMachineType(null) (it NPEs). Just leave it null.

      ScheduledJob sj = scheduledJobInitialized(tg, m, job);

      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::machineTypeMismatch)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void machineTypeMismatch_whenMachineTypeNull_penalizes() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);

      Machine m = machine("m-1", null); // hit: sj.getMachine().getType() == null -> true

      MachineType required = new MachineType();
      required.setMachineTypeUuid("mt-req");
      Job job = jobWithDurationMinutes(TimeGrain.GRAIN_LENGTH_IN_MINUTES);
      job.setRequiredMachineType(required);

      ScheduledJob sj = scheduledJobInitialized(tg, m, job);

      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::machineTypeMismatch)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void machineConflict_whenEitherMachineNull_returnsNoPenalty() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);
      Machine m = machine("m-1", null);

      ScheduledJob a = scheduledJobInitialized(tg, m, jobWithDurationMinutes(10));
      ScheduledJob b = scheduledJobInitialized(tg, m, jobWithDurationMinutes(10));

      a.setMachine(null); // hit: sj1.getMachine() == null

      Schedule sol = solutionWith(List.of(a, b), List.of(tg), List.of(m));

      verifier.verifyThat(ScheduleConstraintProvider::machineConflict)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void machineConflict_whenEitherStartingTimeNull_returnsNoPenalty() {
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);
      Machine m = machine("m-1", null);

      ScheduledJob a = scheduledJobInitialized(tg, m, jobWithDurationMinutes(10));
      ScheduledJob b = scheduledJobInitialized(tg, m, jobWithDurationMinutes(10));

      a.setStartingTimeGrain(null); // hit: sj1.getStartingTimeGrain() == null

      Schedule sol = solutionWith(List.of(a, b), List.of(tg), List.of(m));

      verifier.verifyThat(ScheduleConstraintProvider::machineConflict)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void machineConflict_whenOverlaps_penalizes() {
      // a: starts 0 duration 2 grains (10 minutes), b: starts 1 duration 1 grain =>
      // overlap
      Machine m = machine("m-1", null);
      TimeGrain tg0 = timeGrain(LocalDate.of(2026, 1, 5), 0, ScheduledJob.START_HOUR * 60);
      TimeGrain tg1 = timeGrain(LocalDate.of(2026, 1, 5), 1, ScheduledJob.START_HOUR * 60 + 5);

      ScheduledJob a = scheduledJobInitialized(tg0, m, jobWithDurationMinutes(10)); // 2 grains
      ScheduledJob b = scheduledJobInitialized(tg1, m, jobWithDurationMinutes(5)); // 1 grain

      Schedule sol = solutionWith(List.of(a, b), List.of(tg0, tg1), List.of(m));

      verifier.verifyThat(ScheduleConstraintProvider::machineConflict)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void jobMustFinishBeforeDeadline_whenStartingTimeNull_returnsNoPenalty() {
      Machine m = machine("m-1", null);
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);

      Job job = jobWithDurationMinutes(10);
      job.setDeadline(LocalDateTime.of(2026, 1, 5, 8, 0));

      ScheduledJob sj = scheduledJobInitialized(tg, m, job);
      sj.setStartingTimeGrain(null); // hit: startingTimeGrain == null

      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::jobMustFinishBeforeDeadline)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void jobMustFinishBeforeDeadline_whenJobNull_returnsNoPenalty() {
      Machine m = machine("m-1", null);
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);

      ScheduledJob sj = new ScheduledJob();
      sj.setScheduledJobUuid("sj-" + UUID.randomUUID());
      sj.setStartingTimeGrain(tg);
      sj.setCompatibleMachines(List.of(m));
      sj.setMachine(m);
      sj.setJob(null); // hit: sj.getJob() == null

      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::jobMustFinishBeforeDeadline)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void jobMustFinishBeforeDeadline_whenDeadlineNull_returnsNoPenalty() {
      Machine m = machine("m-1", null);
      TimeGrain tg = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);

      Job job = jobWithDurationMinutes(10);
      job.setDeadline(null); // hit: deadline null

      ScheduledJob sj = scheduledJobInitialized(tg, m, job);
      Schedule sol = solutionWith(sj, tg, m);

      verifier.verifyThat(ScheduleConstraintProvider::jobMustFinishBeforeDeadline)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void preferOneGrainGapBetweenJobs_whenEitherStartingNull_returnsNoPenalty() {
      Machine m = machine("m-1", null);
      TimeGrain tg0 = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);

      ScheduledJob a = scheduledJobInitialized(tg0, m, jobWithDurationMinutes(10));
      ScheduledJob b = scheduledJobInitialized(tg0, m, jobWithDurationMinutes(10));
      b.setStartingTimeGrain(null); // hit: sj2 starting null -> filter returns false

      Schedule sol = solutionWith(List.of(a, b), List.of(tg0), List.of(m));

      verifier.verifyThat(ScheduleConstraintProvider::preferOneGrainGapBetweenJobs)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void preferOneGrainGapBetweenJobs_whenOverlapping_ignoredHere_returnsNoPenalty() {
      Machine m = machine("m-1", null);
      TimeGrain tg0 = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);
      TimeGrain tg1 = timeGrain(LocalDate.of(2026, 1, 5), 1, 7 * 60 + 5);

      ScheduledJob a = scheduledJobInitialized(tg0, m, jobWithDurationMinutes(10)); // 2 grains
      ScheduledJob b = scheduledJobInitialized(tg1, m, jobWithDurationMinutes(5)); // overlaps

      Schedule sol = solutionWith(List.of(a, b), List.of(tg0, tg1), List.of(m));

      // filter returns !overlapsWith -> false, so no penalty here
      verifier.verifyThat(ScheduleConstraintProvider::preferOneGrainGapBetweenJobs)
            .givenSolution(sol)
            .penalizesBy(0);
   }

   @Test
   void preferOneGrainGapBetweenJobs_whenBackToBack_penalizesBy1() {
      // a: [0,2) b: [2,3) => gap = 0 -> penalty 1 (executes gapInGrains and penalty
      // mapping)
      Machine m = machine("m-1", null);

      TimeGrain tg0 = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);
      TimeGrain tg2 = timeGrain(LocalDate.of(2026, 1, 5), 2, 7 * 60 + 10);

      ScheduledJob a = scheduledJobInitialized(tg0, m, jobWithDurationMinutes(10)); // 2 grains
      ScheduledJob b = scheduledJobInitialized(tg2, m, jobWithDurationMinutes(5)); // starts at 2

      Schedule sol = solutionWith(List.of(a, b), List.of(tg0, tg2), List.of(m));

      verifier.verifyThat(ScheduleConstraintProvider::preferOneGrainGapBetweenJobs)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void preferOneGrainGapBetweenJobs_whenBEndsBeforeAStarts_executesOtherOrderBranch_penalizesBy1() {
      // b before a, back-to-back: b [0,1), a [1,2) => still gap 0, but exercises bEnd
      // <= aStart branch
      Machine m = machine("m-1", null);

      TimeGrain tg0 = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);
      TimeGrain tg1 = timeGrain(LocalDate.of(2026, 1, 5), 1, 7 * 60 + 5);

      ScheduledJob b = scheduledJobInitialized(tg0, m, jobWithDurationMinutes(5)); // 1 grain
      ScheduledJob a = scheduledJobInitialized(tg1, m, jobWithDurationMinutes(5)); // starts after b ends

      Schedule sol = solutionWith(List.of(a, b), List.of(tg0, tg1), List.of(m));

      verifier.verifyThat(ScheduleConstraintProvider::preferOneGrainGapBetweenJobs)
            .givenSolution(sol)
            .penalizesBy(1);
   }

   @Test
   void gapInGrains_whenOverlapping_returns0_viaReflection_forCoverage() throws Exception {
      // This directly covers the private helper "overlapping -> 0" branch
      ScheduleConstraintProvider provider = new ScheduleConstraintProvider();

      Machine m = machine("m-1", null);
      TimeGrain tg0 = timeGrain(LocalDate.of(2026, 1, 5), 0, 7 * 60);
      TimeGrain tg1 = timeGrain(LocalDate.of(2026, 1, 5), 1, 7 * 60 + 5);

      ScheduledJob a = scheduledJobInitialized(tg0, m, jobWithDurationMinutes(10)); // [0,2)
      ScheduledJob b = scheduledJobInitialized(tg1, m, jobWithDurationMinutes(10)); // [1,3) overlaps

      Method method = ScheduleConstraintProvider.class.getDeclaredMethod("gapInGrains", ScheduledJob.class,
            ScheduledJob.class);
      method.setAccessible(true);

      int gap = (int) method.invoke(provider, a, b);

      assertEquals(0, gap);
   }

   // ----------------------------
   // Existing helpers (plus a couple small overloads)
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

   private Schedule solutionWith(List<ScheduledJob> jobs, List<TimeGrain> grains, List<Machine> machines) {
      Schedule schedule = new Schedule();
      schedule.setScheduleUuid("s-" + UUID.randomUUID());
      schedule.setWeekStartDate(LocalDateTime.of(2026, 1, 5, 0, 0));

      schedule.setScheduledJobList(jobs);
      schedule.setTimeGrainList(grains);
      schedule.setMachineList(machines);
      schedule.setJobList(jobs.stream().map(ScheduledJob::getJob).filter(j -> j != null).toList());

      return schedule;
   }

   private ScheduledJob scheduledJobInitialized(TimeGrain tg, Machine machine, Job job) {
      ScheduledJob sj = new ScheduledJob();
      sj.setScheduledJobUuid("sj-" + UUID.randomUUID());
      sj.setJob(job);
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
      tg.setDate(date);
      tg.setGrainIndex(grainIndex);
      tg.setStartingMinuteOfDay(startingMinuteOfDay);
      return tg;
   }
}
