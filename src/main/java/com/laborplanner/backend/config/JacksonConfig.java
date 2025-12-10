package com.laborplanner.backend.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

   @Bean
   public Jackson2ObjectMapperBuilder jacksonBuilder() {
      Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

      // JavaTimeModule must be registered so LocalDateTime/Instant/etc are supported
      JavaTimeModule javaTimeModule = new JavaTimeModule();

      // We no longer register a Duration deserializer; durations are integers
      // (minutes) in DTOs
      SimpleModule customModule = new SimpleModule();

      // Register JavaTimeModule and any other modules you need
      builder.modules(javaTimeModule, customModule);

      // Use ISO strings for dates instead of timestamps
      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      return builder;
   }
}
