package com.laborplanner.backend.exception.job;

public class JobNotFoundException extends RuntimeException {
  public JobNotFoundException(String uuid) {
    super("Job not found: " + uuid);
  }
}
