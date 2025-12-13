package com.laborplanner.backend.dto.schedule;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolveWeekRequest {
   private LocalDateTime weekStart;
   private LocalDateTime weekEnd;
}
