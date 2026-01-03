package com.laborplanner.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// @Profile("dev") // This allows me to push this file safely so it's only
// active in development
public class DevCorsConfig {

   @Bean
   public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurer() {
         @Override
         public void addCorsMappings(CorsRegistry registry) {
            registry
                  .addMapping("/api/**")
                  .allowedOrigins("http://localhost:5173")
                  .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
         }
      };
   }
}
