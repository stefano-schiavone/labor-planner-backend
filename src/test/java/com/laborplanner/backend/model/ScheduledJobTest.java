package com.laborplanner.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ScheduledJobTest {

   @Test
   void getDurationInGrains_whenJobNull_returns1() {
      ScheduledJob sj = new ScheduledJob();
      sj.setJob(null);

      assertEquals(1, sj.getDurationInGrains());
   }

   @Test
   void getDurationInGrains_whenDurationNull_returns1() {
      Job job = new Job();
      job.setDurationMinutes(null);

      ScheduledJob sj = new ScheduledJob();
      sj.setJob(job);

      assertEquals(1, sj.getDurationInGrains());
   }

   @Test
   void getDurationInGrains_roundsUpToNextGrain() {
      // grain = 5 minutes
      Job job = new Job();
      job.setDurationMinutes(6); // should round up to 2 grains

      ScheduledJob sj = new ScheduledJob();
      sj.setJob(job);

      assertEquals(2, sj.getDurationInGrains());
   }

   @Test
   void getDurationInGrains_exactMultipleOfGrain() {
      Job job = new Job();
      job.setDurationMinutes(10); // 10/5 = 2 grains

      ScheduledJob sj = new ScheduledJob();
      sj.setJob(job);

      assertEquals(2, sj.getDurationInGrains());
   }

   @Test
   void getEndTimeGrainIndex_whenStartingTimeGrainNull_returnsNull() {
      ScheduledJob sj = new ScheduledJob();
      sj.setStartingTimeGrain(null);

      assertNull(sj.getEndTimeGrainIndex());
   }

   @Test
   void getEndTimeGrainIndex_whenStartingTimeGrainPresent_returnsStartPlusDuration() {
      Job job = new Job();
      job.setDurationMinutes(10); // 2 grains

      TimeGrain tg = new TimeGrain();
      tg.setGrainIndex(7);
      tg.setStartingMinuteOfDay(ScheduledJob.START_HOUR * 60);
      tg.setDate(LocalDate.of(2026, 1, 5));

      ScheduledJob sj = new ScheduledJob();
      sj.setJob(job);
      sj.setStartingTimeGrain(tg);

      assertEquals(9, sj.getEndTimeGrainIndex());
   }

   @Test
   void overlapsWith_whenOtherNull_returnsFalse() {
      ScheduledJob sj = new ScheduledJob();
      sj.setStartingTimeGrain(null);

      assertFalse(sj.overlapsWith(null));
   }

   @Test
   void overlapsWith_whenEitherStartingTimeMissing_returnsFalse() {
      TimeGrain tg = new TimeGrain();
      tg.setGrainIndex(0);
      tg.setStartingMinuteOfDay(ScheduledJob.START_HOUR * 60);
      tg.setDate(LocalDate.of(2026, 1, 5));

      ScheduledJob a = new ScheduledJob();
      a.setStartingTimeGrain(null);

      ScheduledJob b = new ScheduledJob();
      b.setStartingTimeGrain(tg);

      assertFalse(a.overlapsWith(b));
      assertFalse(b.overlapsWith(a));
   }

   @Test
   void overlapsWith_whenIntervalsOverlap_returnsTrue() {
      // a: [0, 2)
      Job jobA = new Job();
      jobA.setDurationMinutes(10); // 2 grains

      TimeGrain tgA = new TimeGrain();
      tgA.setGrainIndex(0);

      ScheduledJob a = new ScheduledJob();
      a.setJob(jobA);
      a.setStartingTimeGrain(tgA);

      // b: [1, 2)
      Job jobB = new Job();
      jobB.setDurationMinutes(5); // 1 grain

      TimeGrain tgB = new TimeGrain();
      tgB.setGrainIndex(1);

      ScheduledJob b = new ScheduledJob();
      b.setJob(jobB);
      b.setStartingTimeGrain(tgB);

      assertTrue(a.overlapsWith(b));
      assertTrue(b.overlapsWith(a));
   }

   @Test
   void overlapsWith_whenBackToBack_doesNotOverlap() {
      // a: [0, 2)
      Job jobA = new Job();
      jobA.setDurationMinutes(10); // 2 grains

      TimeGrain tgA = new TimeGrain();
      tgA.setGrainIndex(0);

      ScheduledJob a = new ScheduledJob();
      a.setJob(jobA);
      a.setStartingTimeGrain(tgA);

      // b: [2, 3)
      Job jobB = new Job();
      jobB.setDurationMinutes(5); // 1 grain

      TimeGrain tgB = new TimeGrain();
      tgB.setGrainIndex(2);

      ScheduledJob b = new ScheduledJob();
      b.setJob(jobB);
      b.setStartingTimeGrain(tgB);

      assertFalse(a.overlapsWith(b));
      assertFalse(b.overlapsWith(a));
   }

   @Test
   void setCompatibleMachines_setsField() {
      ScheduledJob sj = new ScheduledJob();
      Machine m = new Machine();
      m.setMachineUuid("m1");

      sj.setCompatibleMachines(java.util.List.of(m));

      assertNotNull(sj.getCompatibleMachines());
      assertEquals(1, sj.getCompatibleMachines().size());
      assertEquals("m1", sj.getCompatibleMachines().get(0).getMachineUuid());
   }
}
