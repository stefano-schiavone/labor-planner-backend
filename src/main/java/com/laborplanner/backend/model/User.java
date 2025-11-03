package com.laborplanner.backend.model;

import lombok.*;

@Getter
@Setter
@Data
public class User {

  // Fields
  private String userUuid;

  private String name;

  private String lastName;

  private String email;

  private AccountType type;

  // Constructors
  // Constructor with all arguments except UUID
  public User(String name, String lastName, String email, AccountType type) {
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.type = type;
  }
}
