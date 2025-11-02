package com.laborplanner.backend.controller;

import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.service.JobTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-templates")
@Tag(name = "Job Templates", description = "Manage job template definitions")
public class JobTemplateController {

  @Autowired private JobTemplateService jobTemplateService;

  @Operation(summary = "Get all job templates")
  @GetMapping
  public List<JobTemplate> getAllJobTemplates() {
    return jobTemplateService.getAllJobTemplates();
  }

  @Operation(summary = "Get a job template by UUID")
  @GetMapping("/{uuid}")
  public JobTemplate getJobTemplate(@PathVariable String uuid) {
    return jobTemplateService.getJobTemplateByUuid(uuid);
  }

  @Operation(summary = "Create a new job template")
  @PostMapping
  public JobTemplate createJobTemplate(@RequestBody JobTemplate jobTemplate) {
    return jobTemplateService.createJobTemplate(jobTemplate);
  }

  @Operation(summary = "Update a job template")
  @PutMapping("/{uuid}")
  public JobTemplate updateJobTemplate(
      @PathVariable String uuid, @RequestBody JobTemplate updatedJobTemplate) {
    return jobTemplateService.updateJobTemplate(uuid, updatedJobTemplate);
  }

  @Operation(summary = "Delete a job template")
  @DeleteMapping("/{uuid}")
  public void deleteJobTemplate(@PathVariable String uuid) {
    jobTemplateService.deleteJobTemplate(uuid);
  }
}
