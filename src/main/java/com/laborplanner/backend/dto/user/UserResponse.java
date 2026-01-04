package com.laborplanner.backend.dto.user;

import com.laborplanner.backend.model.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
   private String userUuid;
   private String name;
   private String lastName;
   private String email;
   private AccountType type;

   public UserResponse(String userUuid, String name, String lastName, String email, AccountType type) {
      this.userUuid = userUuid;
      this.name = name;
      this.lastName = lastName;
      this.email = email;
      this.type = type;
   }
}
