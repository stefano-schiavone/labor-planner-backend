package com.laborplanner.backend.dto.scheduledJob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledJobConflictResponse {
   private String type;
   private String message;
   private String scheduleUuid;
   private String weekStartDate;
}
