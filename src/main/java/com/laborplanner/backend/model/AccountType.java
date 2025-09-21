package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "account_type")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
@AllArgsConstructor
public class AccountType {

  // Fields
  @Id
  @Column(name = "account_type_uuid", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String accountTypeUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public AccountType(String name) {
    this.name = name;
  }
}
