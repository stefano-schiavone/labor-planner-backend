package com.laborplanner.backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user")
public class User {

  // Fields
  @Id
  @Column(name = "user_uuid", nullable = false, updatable = false)
  private String userUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  // Many users can share one account type
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_type_uuid", nullable = false)
  private AccountType type;

  // Constructors
  public User() {}

  public User(String name, String lastName, String email, AccountType type) {
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.type = type;
  }

  // Getters & Setters
  public String getUserUuid() {
    return this.userUuid;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public AccountType getType() {
    return this.type;
  }

  public void setType(AccountType type) {
    this.type = type;
  }
}
