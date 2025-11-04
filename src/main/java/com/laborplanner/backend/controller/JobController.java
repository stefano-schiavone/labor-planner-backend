package com.laborplanner.backend.controller;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Manage job definitions and scheduling")
public class JobController {

  @Autowired private JobService jobService;

  @Operation(summary = "Get all jobs")
  @GetMapping
  public List<Job> getAllJobs() {
    return jobService.getAllJobs();
  }

  @Operation(summary = "Get a job by UUID")
  @GetMapping("/{uuid}")
  public Job getJob(@PathVariable String uuid) {
    return jobService.getJobByUuid(uuid);
  }

  @Operation(summary = "Create a new job")
  @PostMapping
  public Job createJob(@RequestBody Job job) {
    return jobService.createJob(job);
  }

  @Operation(summary = "Update a job")
  @PutMapping("/{uuid}")
  public Job updateJob(@PathVariable String uuid, @RequestBody Job updatedJob) {
    return jobService.updateJob(uuid, updatedJob);
  }

  @Operation(summary = "Delete a job")
  @DeleteMapping("/{uuid}")
  public void deleteJob(@PathVariable String uuid) {
    jobService.deleteJob(uuid);
  }
}
