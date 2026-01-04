package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.dto.user.CreateUserRequest;
import com.laborplanner.backend.model.User;

public interface IUserAuthService {
   User getUserByEmail(String email);

   User createUser(CreateUserRequest user);

   User updateUser(String uuid, User updatedUser);
}
