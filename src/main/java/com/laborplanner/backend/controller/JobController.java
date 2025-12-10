package com.laborplanner.backend.controller;

import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.dto.job.JobRequest;
import com.laborplanner.backend.dto.job.JobResponse;
import com.laborplanner.backend.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Manage job definitions and scheduling")
public class JobController {

   private final JobService jobService;

   public JobController(JobService jobService) {
      this.jobService = jobService;
   }

   // -------------------------------------------------------
   // CRUD Endpoints
   // -------------------------------------------------------

   @Operation(summary = "Get all jobs")
   @GetMapping
   public List<JobResponse> getAllJobs() {
      return jobService.getAllJobs().stream().map(this::toResponse).collect(Collectors.toList());
   }

   @Operation(summary = "Get a job by UUID")
   @GetMapping("/{uuid}")
   public JobResponse getJob(@PathVariable String uuid) {
      return toResponse(jobService.getJobByUuid(uuid));
   }

   @Operation(summary = "Create a new job")
   @PostMapping
   public ResponseEntity<JobResponse> createJob(@RequestBody @Valid JobRequest request) {
      JobDto dto = toDto(request);

      // service returns JobDto
      JobDto created = jobService.createJob(dto);

      JobResponse response = toResponse(created);
      URI location = URI.create("/api/jobs/" + created.getJobUuid());
      return ResponseEntity.created(location).body(response);
   }

   @Operation(summary = "Update a job")
   @PutMapping("/{uuid}")
   public JobResponse updateJob(@PathVariable String uuid, @RequestBody @Valid JobRequest request) {

      JobDto dto = toDto(request);
      JobDto updated = jobService.updateJob(uuid, dto);

      return toResponse(updated);
   }

   @Operation(summary = "Delete a job")
   @DeleteMapping("/{uuid}")
   public void deleteJob(@PathVariable String uuid) {
      jobService.deleteJob(uuid);
   }

   // -------------------------------------------------------
   // Mapping Helpers
   // -------------------------------------------------------

   private JobResponse toResponse(JobDto dto) {
      return new JobResponse(
            dto.getJobUuid(),
            dto.getTemplateUuid(),
            dto.getName(),
            dto.getDescription(),
            dto.getDuration(),
            dto.getDeadline(),
            dto.getRequiredMachineTypeUuid());
   }

   private JobDto toDto(JobRequest req) {
      return new JobDto(
            req.getTemplateUuid(),
            req.getName(),
            req.getDescription(),
            req.getDurationAsDuration(),
            req.getDeadline(),
            req.getRequiredMachineTypeUuid());
   }
}
