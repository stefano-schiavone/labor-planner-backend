package com.laborplanner.backend.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laborplanner.backend.controller.JobController;
import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.dto.job.JobRequest;
import com.laborplanner.backend.exception.GlobalExceptionHandler;
import com.laborplanner.backend.service.JobService;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit-level WebMvcTest that verifies validation constraints in JobController. Since it's a
 * controller with little to no logic it will focus on: @Valid, @NotBlank, @NotNull behavior.
 */
@WebMvcTest(JobController.class)
@Import(GlobalExceptionHandler.class)
class JobControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private JobService jobService;

  @Test
  void createJob_withValidRequest_returnsCreated() throws Exception {
    JobRequest request = new JobRequest();
    request.setName("Valid Job");
    request.setDescription("This is a valid job");
    request.setDuration(Duration.ofHours(2));
    request.setDeadline(LocalDateTime.now().plusDays(1));
    request.setRequiredMachineTypeUuid("machine-uuid");

    JobDto createdDto =
        new JobDto(
            "template-uuid",
            request.getName(),
            request.getDescription(),
            request.getDuration(),
            request.getDeadline(),
            request.getRequiredMachineTypeUuid());
    createdDto.setJobUuid("job-uuid-1");

    when(jobService.createJob(any(JobDto.class))).thenReturn(createdDto);

    mockMvc
        .perform(
            post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/jobs/job-uuid-1"))
        .andExpect(jsonPath("$.jobUuid").value("job-uuid-1"));
  }

  @Test
  void createJob_withMissingFields_returnsBadRequest() throws Exception {
    JobRequest request = new JobRequest();
    // All fields left null or blank to trigger validation errors
    request.setName("");
    request.setDescription("");
    request.setDuration(null);
    request.setDeadline(null);
    request.setRequiredMachineTypeUuid("");

    mockMvc
        .perform(
            post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createJob_withBlankName_returnsBadRequest() throws Exception {
    JobRequest request = new JobRequest();
    request.setName(""); // invalid
    request.setDescription("Missing name test");
    request.setDuration(Duration.ofMinutes(30));
    request.setDeadline(LocalDateTime.now().plusDays(1));
    request.setRequiredMachineTypeUuid("machine-uuid");

    mockMvc
        .perform(
            post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createJob_withMissingMachineType_returnsBadRequest() throws Exception {
    JobRequest request = new JobRequest();
    request.setName("Job Without MachineType");
    request.setDescription("Should fail due to missing machine type");
    request.setDuration(Duration.ofHours(1));
    request.setDeadline(LocalDateTime.now().plusDays(1));
    request.setRequiredMachineTypeUuid(""); // invalid

    mockMvc
        .perform(
            post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
