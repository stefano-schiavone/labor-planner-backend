package com.laborplanner.backend.dto.user;

import com.laborplanner.backend.model.AccountType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
   private String name;
   private String lastName;
   private String email;
   private String password; // plaintext for creation, will be hashed in service
   private AccountType type;
}
