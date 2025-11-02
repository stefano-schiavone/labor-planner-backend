package com.laborplanner.backend.exception.job;

public class DuplicateJobTemplateNameException extends RuntimeException {
  public DuplicateJobTemplateNameException(String name) {
    super("Job template with name '" + name + "' already exists.");
  }
}
