package com.laborplanner.backend.exception.job;

public class JobTemplateNotFoundException extends RuntimeException {
  public JobTemplateNotFoundException(String uuid) {
    super("Job template not found with UUID: " + uuid);
  }
}
