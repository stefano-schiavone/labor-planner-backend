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
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.repository.ScheduleRepository;
import com.laborplanner.backend.service.JobService;
import com.laborplanner.backend.service.JwtService;
import com.laborplanner.backend.service.interfaces.IJobTemplateReadService;
import com.laborplanner.backend.service.interfaces.IMachineTypeReadService;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
// import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
// import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
// import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit-level WebMvcTest that verifies validation constraints in JobController.
 * Since it's a
 * controller with little to no logic it will focus
 * on: @Valid, @NotBlank, @NotNull behavior.
 */
@WebMvcTest(controllers = JobController.class)
// @WebMvcTest(controllers = JobController.class, excludeAutoConfiguration = {
// SecurityAutoConfiguration.class,
// SecurityFilterAutoConfiguration.class,
// OAuth2ResourceServerAutoConfiguration.class,
// OAuth2ClientAutoConfiguration.class
// })
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper objectMapper;

   @MockitoBean
   private JobService jobService;

   @MockitoBean
   private JobRepository jobRepository;

   @MockitoBean
   private ScheduleRepository scheduleRepository;

   @MockitoBean
   private IMachineTypeReadService machineTypeService;

   @MockitoBean
   private IJobTemplateReadService jobTemplateService;

   @MockitoBean
   private JwtService jwtService;

   @Test
   void createJob_withValidRequest_returnsCreated() throws Exception {
      JobRequest request = new JobRequest();
      request.setName("Valid Job");
      request.setDescription("This is a valid job");
      request.setDurationMinutes(120);
      request.setDeadline(LocalDateTime.now().plusDays(1));
      request.setRequiredMachineTypeUuid("machine-uuid");

      JobDto createdDto = new JobDto(
            "template-uuid",
            request.getName(),
            request.getDescription(),
            request.getDurationMinutes(),
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
      request.setDurationMinutes(null);
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
      request.setDurationMinutes(30);
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
      request.setDurationMinutes(60);
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
