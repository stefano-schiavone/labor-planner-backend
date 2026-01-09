package com.laborplanner.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("test")
public class TestDataLoader {

   @Bean
   CommandLineRunner loadTestData(JdbcTemplate jdbcTemplate) {
      return args -> {
         System.out.println("🔄 Loading test data...");

         try {
            // Insert account type
            jdbcTemplate.update(
                  "INSERT INTO account_type (account_type_uuid, name) VALUES (?, ?)",
                  java.util.UUID.fromString("0d5965dc-5fda-4441-98e8-8fcf6b1ddfb6"),
                  "admin");
            System.out.println("✅ Account type inserted");

            // Insert test user
            jdbcTemplate.update(
                  "INSERT INTO app_user (user_uuid, email, last_name, name, account_type_uuid, password_hash) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                  java.util.UUID.fromString("d41579bf-dec8-431f-a99a-fb770f45d7fb"),
                  "stefanocloud32@gmail.com",
                  "Schiavone",
                  "Stefano",
                  java.util.UUID.fromString("0d5965dc-5fda-4441-98e8-8fcf6b1ddfb6"),
                  "$2a$10$MBpQD7dTy8yuI8AycKw/fuun9M4lzFyhiwjwO5RrZSSzfaH2TowTO");
            System.out.println("✅ Test user inserted:  stefanocloud32@gmail.com");

            // Verify
            Integer count = jdbcTemplate.queryForObject(
                  "SELECT COUNT(*) FROM app_user WHERE email = ?",
                  Integer.class,
                  "stefanocloud32@gmail.com");
            System.out.println("✅ Test data loaded successfully.  User count: " + count);

         } catch (Exception e) {
            System.err.println("❌ Failed to load test data: " + e.getMessage());
            e.printStackTrace();
         }
      };
   }
}
