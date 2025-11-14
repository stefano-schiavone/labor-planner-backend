package com.laborplanner.backend.model;

import java.util.Comparator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeGrain implements Comparable<TimeGrain> {

  private static final Comparator<TimeGrain> COMPARATOR =
      Comparator.comparingInt(TimeGrain::getStartingMinuteOfDay);

  /**
   * Time granularity is 5 minutes (Documentation suggested 15, but for better UX I'm using 5. It
   * might take longer).
   */
  public static final int GRAIN_LENGTH_IN_MINUTES = 5;

  private int grainIndex;
  private int startingMinuteOfDay;

  @Override
  public int compareTo(TimeGrain other) {
    return COMPARATOR.compare(this, other);
  }
}
