package com.laborplanner.backend.service.interfaces;

import java.time.LocalDateTime;

import com.laborplanner.backend.dto.schedule.ScheduleDto;
import com.laborplanner.backend.model.Schedule;

public interface IScheduleService {

   ScheduleDto getScheduleForWeek(LocalDateTime weekStart, LocalDateTime weekEnd);

   Schedule solveSchedule(Schedule problem);

   ScheduleDto solveForWeek(LocalDateTime weekStart, LocalDateTime weekEnd);

   void deleteSchedule(String uuid);
}
