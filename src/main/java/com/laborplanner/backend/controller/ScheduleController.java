package com.laborplanner.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laborplanner.backend.dto.schedule.SolveWeekRequest;
import com.laborplanner.backend.dto.schedule.WeekScheduleResponse;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.service.interfaces.IJobService;
import com.laborplanner.backend.service.interfaces.IScheduleService;

@RestController
@RequestMapping("api/schedules")
public class ScheduleController {

   private IScheduleService scheduleService;
   private IJobService jobService;

   public ScheduleController(IScheduleService scheduleService, IJobService jobService) {
      this.scheduleService = scheduleService;
      this.jobService = jobService;
   }

   @PostMapping("/for-week")
   public WeekScheduleResponse getScheduleForWeek(@RequestBody SolveWeekRequest request) {
      var response = new WeekScheduleResponse();
      var schedule = scheduleService.getScheduleForWeek(request.getWeekStart(), request.getWeekEnd());

      if (schedule != null) {
         response.setExists(true);
         response.setSchedule(schedule);
         response.setScheduledJobs(schedule.getScheduledJobList());
      } else {
         response.setExists(false);
         response.setCandidateJobs(
               jobService.findJobsInDeadlineRange(request.getWeekStart(), request.getWeekEnd()));
      }

      return response;
   }

   @PostMapping("/solve")
   public Schedule solve(@RequestBody Schedule problem) {
      return scheduleService.solveSchedule(problem);
   }

   @PostMapping("/solve-for-week")
   public Schedule solveForWeek(@RequestBody SolveWeekRequest request) {
      return scheduleService.solveForWeek(
            request.getWeekStart(),
            request.getWeekEnd());
   }
}
