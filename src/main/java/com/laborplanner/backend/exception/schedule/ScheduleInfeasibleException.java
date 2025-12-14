package com.laborplanner.backend.exception.schedule;

public class ScheduleInfeasibleException extends RuntimeException {
   public ScheduleInfeasibleException() {
      super("No feasible solution found");
   }
}
