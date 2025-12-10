package com.laborplanner.backend.config;

import com.laborplanner.backend.json.DurationDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Duration;

@Configuration
public class JacksonConfig {

   @Bean
   public Jackson2ObjectMapperBuilder jacksonBuilder() {
      Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

      // JavaTimeModule must be registered so LocalDateTime/Instant/etc are supported
      JavaTimeModule javaTimeModule = new JavaTimeModule();

      // Custom module to override Duration deserializer (register this AFTER
      // JavaTimeModule)
      SimpleModule durationModule = new SimpleModule();
      durationModule.addDeserializer(Duration.class, new DurationDeserializer());

      // Register JavaTimeModule first, then our custom durationModule so our
      // deserializer wins
      builder.modules(javaTimeModule, durationModule);

      // Use ISO strings for dates instead of timestamps
      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      return builder;
   }
}
