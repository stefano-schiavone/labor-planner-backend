package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.model.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {

  // Find a user by exact email
  Optional<User> findByEmail(String email);

  // Check if a user with the given email exists
  boolean existsByEmail(String email);
}
