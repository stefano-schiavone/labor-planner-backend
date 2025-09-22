package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByEmail(String email);

  // Faster and more optimized method of checking if user exists
  boolean existsByEmail(String email);
}
