package com.laborplanner.backend.schedule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laborplanner.backend.controller.ScheduleController;
import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.dto.schedule.ScheduleDto;
import com.laborplanner.backend.dto.schedule.SolveWeekRequest;
import com.laborplanner.backend.exception.GlobalExceptionHandler;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.service.JwtService;
import com.laborplanner.backend.service.interfaces.IJobService;
import com.laborplanner.backend.service.interfaces.IScheduleService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ScheduleController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper objectMapper;

   @MockitoBean
   private IScheduleService scheduleService;

   @MockitoBean
   private IJobService jobService;

   // Present in your other controller tests; include to satisfy context if
   // security config references it.
   @MockitoBean
   private JwtService jwtService;

   @Test
   void getScheduleForWeek_whenScheduleExists_returnsExistsTrueWithJobs() throws Exception {
      LocalDateTime weekStart = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime weekEnd = weekStart.plusDays(7);

      SolveWeekRequest request = new SolveWeekRequest();
      request.setWeekStart(weekStart);
      request.setWeekEnd(weekEnd);

      ScheduledJob sj = new ScheduledJob();
      sj.setScheduledJobUuid("sj-1");

      ScheduleDto scheduleDto = new ScheduleDto(
            "sched-1",
            weekStart,
            weekStart.plusHours(1),
            List.of(), // ScheduledJobDto list not asserted here
            List.of());
      // Controller uses schedule.getScheduledJobList(); in your DTO this method
      // exists and returns list of ScheduledJobDto
      // (it is named "scheduledJobList" in your service mapping). We only need
      // non-null for JSON shape.
      // If your ScheduleDto exposes getScheduledJobList() as ScheduledJobDto, it's
      // OK; we check exists + scheduleUuid.

      when(scheduleService.getScheduleForWeek(eq(weekStart), eq(weekEnd))).thenReturn(scheduleDto);

      mockMvc.perform(
            post("/api/schedules/for-week")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(true))
            .andExpect(jsonPath("$.schedule.scheduleUuid").value("sched-1"));
   }

   @Test
   void getScheduleForWeek_whenScheduleMissing_returnsExistsFalseWithCandidateJobs() throws Exception {
      LocalDateTime weekStart = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime weekEnd = weekStart.plusDays(7);

      SolveWeekRequest request = new SolveWeekRequest();
      request.setWeekStart(weekStart);
      request.setWeekEnd(weekEnd);

      when(scheduleService.getScheduleForWeek(eq(weekStart), eq(weekEnd))).thenReturn(null);

      JobDto j1 = new JobDto(
            null,
            null,
            "Job-1",
            "Desc",
            30,
            weekStart.plusDays(1),
            "mt-1");
      j1.setJobUuid("j1");

      when(jobService.findJobsInDeadlineRange(eq(weekStart), eq(weekEnd))).thenReturn(List.of(j1));

      mockMvc.perform(
            post("/api/schedules/for-week")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(false))
            .andExpect(jsonPath("$.candidateJobs").isArray())
            .andExpect(jsonPath("$.candidateJobs[0].jobUuid").value("j1"));
   }

   @Test
   void solve_returnsScheduleFromService() throws Exception {
      Schedule problem = new Schedule();
      problem.setScheduleUuid("problem-1");

      Schedule solved = new Schedule();
      solved.setScheduleUuid("solved-1");

      when(scheduleService.solveSchedule(any(Schedule.class))).thenReturn(solved);

      mockMvc.perform(
            post("/api/schedules/solve")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(problem)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scheduleUuid").value("solved-1"));
   }

   @Test
   void solveForWeek_whenScheduleReturned_existsTrue() throws Exception {
      LocalDateTime weekStart = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime weekEnd = weekStart.plusDays(7);

      SolveWeekRequest request = new SolveWeekRequest();
      request.setWeekStart(weekStart);
      request.setWeekEnd(weekEnd);

      ScheduleDto scheduleDto = new ScheduleDto(
            "sched-2",
            weekStart,
            weekStart.plusHours(2),
            List.of(),
            List.of());

      when(scheduleService.solveForWeek(eq(weekStart), eq(weekEnd))).thenReturn(scheduleDto);

      mockMvc.perform(
            post("/api/schedules/solve-for-week")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(true))
            .andExpect(jsonPath("$.schedule.scheduleUuid").value("sched-2"));
   }

   @Test
   void solveForWeek_whenScheduleNull_existsFalseAndCandidateJobs() throws Exception {
      LocalDateTime weekStart = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime weekEnd = weekStart.plusDays(7);

      SolveWeekRequest request = new SolveWeekRequest();
      request.setWeekStart(weekStart);
      request.setWeekEnd(weekEnd);

      when(scheduleService.solveForWeek(eq(weekStart), eq(weekEnd))).thenReturn(null);

      JobDto j1 = new JobDto(
            null,
            null,
            "Job-1",
            "Desc",
            30,
            weekStart.plusDays(1),
            "mt-1");
      j1.setJobUuid("j1");

      when(jobService.findJobsInDeadlineRange(eq(weekStart), eq(weekEnd))).thenReturn(List.of(j1));

      mockMvc.perform(
            post("/api/schedules/solve-for-week")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(false))
            .andExpect(jsonPath("$.candidateJobs[0].jobUuid").value("j1"));
   }

   @Test
   void deleteSchedule_returnsNoContent_andCallsService() throws Exception {
      mockMvc.perform(delete("/api/schedules/{uuid}", "sched-del-1"))
            .andExpect(status().isNoContent());

      verify(scheduleService).deleteSchedule("sched-del-1");
   }
}
