package com.laborplanner.backend.exception;

import com.laborplanner.backend.dto.scheduledJob.ScheduledJobConflictResponse;
import com.laborplanner.backend.exception.scheduledJob.ScheduledJobConflictException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ScheduledJobExceptionHandler {

   @ExceptionHandler(ScheduledJobConflictException.class)
   public ResponseEntity<ScheduledJobConflictResponse> handleScheduledJobConflict(ScheduledJobConflictException ex) {
      log.warn("Scheduled job conflict:  {}", ex.getMessage());

      // Don't try to fetch the schedule here - just use what's in the exception
      ScheduledJobConflictResponse response = new ScheduledJobConflictResponse(
            "SCHEDULED_JOB_CONFLICT",
            "This job is part of an existing schedule. Please delete the schedule first.",
            ex.getScheduleUuid(),
            null // We'll populate this in the service layer
      );

      return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
   }
}
