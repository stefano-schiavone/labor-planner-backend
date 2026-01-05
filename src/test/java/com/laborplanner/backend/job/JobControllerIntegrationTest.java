package com.laborplanner.backend.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laborplanner.backend.BackendApplication;
import com.laborplanner.backend.dto.job.JobRequest;
import com.laborplanner.backend.exception.GlobalExceptionHandler;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.repository.MachineTypeRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Tag;

@Tag("integration")
@SpringBootTest(classes = BackendApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
class JobControllerIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper objectMapper;

   @Autowired
   private JobRepository jobRepository;

   @Autowired
   private MachineTypeRepository machineTypeRepository;

   @Test
   @Transactional
   @WithMockUser
   void createJob_persistsJobToDatabase() throws Exception {
      // Create MachineType
      MachineType type = new MachineType();
      type.setName("Test Machine");
      MachineType createdType = machineTypeRepository.create(type);

      // Prepare request
      JobRequest request = new JobRequest();
      request.setName("Integration Test Job");
      request.setDescription("Integration test of JobController");
      request.setDurationMinutes(180);
      request.setDeadline(LocalDateTime.now().plusDays(2));
      request.setRequiredMachineTypeUuid(createdType.getMachineTypeUuid());

      // Perform POST request
      mockMvc
            .perform(
                  post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.jobUuid").isNotEmpty())
            .andExpect(jsonPath("$.name").value("Integration Test Job"));

      // Verify persistence
      boolean exists = jobRepository.existsByName("Integration Test Job");
      assertThat(exists).isTrue();
   }

   @Test
   @Transactional
   @WithMockUser
   void createJob_withEmptyMachineTypeUuid_returnsBadRequest() throws Exception {
      // Not setting MachineType

      // Prepare request
      JobRequest request = new JobRequest();
      request.setName("Integration Test Job");
      request.setDescription("Integration test of JobController");
      request.setDurationMinutes(180);
      request.setDeadline(LocalDateTime.now().plusDays(2));
      request.setRequiredMachineTypeUuid("");

      // Perform POST request
      mockMvc
            .perform(
                  post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
   }
}
