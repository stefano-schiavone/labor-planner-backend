package com.laborplanner.backend.model;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountType {

  // Fields
  @Setter(AccessLevel.NONE)
  private String accountTypeUuid = UUID.randomUUID().toString();

  private String name;

  // Constructors
  // Constructor with all arguments except UUID
  public AccountType(String name) {
    this.name = name;
  }
}
