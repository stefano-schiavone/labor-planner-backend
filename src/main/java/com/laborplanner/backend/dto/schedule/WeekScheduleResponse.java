package com.laborplanner.backend.dto.schedule;

import java.util.List;

import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.dto.scheduledJob.ScheduledJobDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeekScheduleResponse {

   private boolean exists; // true if schedule already exists
   private ScheduleDto schedule; // optional, only if exists == true
   private List<ScheduledJobDto> scheduledJobs; // optional
   private List<JobDto> candidateJobs; // optional if schedule does not exist
}
