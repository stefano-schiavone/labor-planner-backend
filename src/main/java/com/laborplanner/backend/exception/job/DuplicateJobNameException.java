package com.laborplanner.backend.exception.job;

public class DuplicateJobNameException extends RuntimeException {
  public DuplicateJobNameException(String name) {
    super("Job name already exists: " + name);
  }
}
