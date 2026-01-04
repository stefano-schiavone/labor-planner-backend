package com.laborplanner.backend.exception.scheduledJob;

public class ScheduledJobConflictException extends RuntimeException {
   private final String scheduleUuid;

   public ScheduledJobConflictException(String jobUuid, String scheduleUuid) {
      super(String.format("Job %s is part of schedule %s and cannot be deleted", jobUuid, scheduleUuid));
      this.scheduleUuid = scheduleUuid;
   }

   public String getScheduleUuid() {
      return scheduleUuid;
   }
}
