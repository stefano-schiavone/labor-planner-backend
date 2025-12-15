package com.laborplanner.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeGrain implements Comparable<TimeGrain> {

   private static final Comparator<TimeGrain> COMPARATOR = Comparator.comparingInt(TimeGrain::getStartingMinuteOfDay);

   /**
    * Time granularity is 5 minutes (Documentation suggested 15, but for better UX
    * I'm using 5. It
    * might take longer).
    */
   public static final int GRAIN_LENGTH_IN_MINUTES = 5;

   private int grainIndex;
   private int startingMinuteOfDay;
   private LocalDate date;

   public TimeGrain(int grainIndex, int startingMinuteOfDay) {
      this.grainIndex = grainIndex;
      this.startingMinuteOfDay = startingMinuteOfDay;
   }

   @Override
   public int compareTo(TimeGrain other) {
      return COMPARATOR.compare(this, other);
   }

   public LocalDateTime toDateTime() {
      return date.atStartOfDay().plusMinutes(startingMinuteOfDay);
   }

   public static TimeGrain fromIndex(int grainIndex, LocalDate weekStart) {
      int grainsPerDay = (ScheduledJob.END_HOUR - ScheduledJob.START_HOUR) * 60
            / GRAIN_LENGTH_IN_MINUTES;

      int dayOffset = grainIndex / grainsPerDay;
      int indexInDay = grainIndex % grainsPerDay;

      LocalDate date = weekStart.plusDays(dayOffset);
      int startingMinute = ScheduledJob.START_HOUR * 60
            + indexInDay * GRAIN_LENGTH_IN_MINUTES;

      return new TimeGrain(grainIndex, startingMinute, date);
   }

}
