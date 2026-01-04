package com.laborplanner.backend.security;

import com.laborplanner.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

   private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
   private final UserRepository userRepository;

   @Override
   public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      logger.debug("Attempting to load user with email: {}", email);

      // Trim whitespace and normalize case
      String trimmedEmail = email.trim().toLowerCase();

      // Check if user exists
      boolean exists = userRepository.existsByEmail(trimmedEmail);
      logger.debug("User exists in DB: {}", exists);

      // Load user or throw exception
      com.laborplanner.backend.model.User user = userRepository.findByEmail(trimmedEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + trimmedEmail));

      logger.debug("Successfully loaded user: {}", user.getEmail());

      return new User(
            user.getEmail(),
            user.getPasswordHash(),
            new ArrayList<>());
   }
}
