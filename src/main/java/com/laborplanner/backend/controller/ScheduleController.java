package com.laborplanner.backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.service.ScheduleService;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

   @Autowired
   private ScheduleService scheduleService;

   @PostMapping("/solve")
   public Schedule solve(@RequestBody Schedule problem) {
      return scheduleService.solveSchedule(problem);
   }
}
