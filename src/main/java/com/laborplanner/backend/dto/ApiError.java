package com.laborplanner.backend.dto;

import java.time.Instant;
import org.springframework.http.HttpStatus;

public record ApiError(String error, String details, int status, Instant timestamp) {
  public static ApiError of(String error, String details, HttpStatus status) {
    return new ApiError(error, details, status.value(), Instant.now());
  }
}
