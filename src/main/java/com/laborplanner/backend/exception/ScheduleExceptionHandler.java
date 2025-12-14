package com.laborplanner.backend.exception;

import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.laborplanner.backend.exception.schedule.ScheduleInfeasibleException;

@RestControllerAdvice
@Order(1)
public class ScheduleExceptionHandler {

   @ExceptionHandler(ScheduleInfeasibleException.class)
   public ResponseEntity<?> handleInfeasibleSchedule(ScheduleInfeasibleException e) {
      return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(Map.of(
                  "type", "INFEASIBLE_SCHEDULE",
                  "message", e.getMessage()));
   }
}
