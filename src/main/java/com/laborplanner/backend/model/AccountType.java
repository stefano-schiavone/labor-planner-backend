package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "account_type")
public class AccountType {

  // Fields
  @Id
  @Column(name = "account_type_uuid", nullable = false, updatable = false)
  private String accountTypeUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  // Constructors
  public AccountType() {}

  public AccountType(String name) {
    this.name = name;
  }

  // Getters & Setters
  public String getAccountTypeUuid() {
    return this.accountTypeUuid;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
