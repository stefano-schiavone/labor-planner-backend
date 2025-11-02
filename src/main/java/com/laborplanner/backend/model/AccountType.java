package com.laborplanner.backend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountType {

  // Fields
  private String accountTypeUuid;

  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public AccountType(String name) {
    this.name = name;
  }
}
