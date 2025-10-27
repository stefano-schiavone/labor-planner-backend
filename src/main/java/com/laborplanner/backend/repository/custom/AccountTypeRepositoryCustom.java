package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.model.AccountType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepositoryCustom {

  // Find by exact name
  Optional<AccountType> findByName(String name);

  // Check if an account type already exists
  boolean existsByName(String name);

  // Get all account types in alphabetical order
  List<AccountType> findAllByOrderByNameAsc();
}
