package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.dto.user.CreateUserRequest;
import com.laborplanner.backend.model.User;
import java.util.List;

public interface IUserService {
   User createUser(CreateUserRequest user);

   User getUserByUuid(String userUuid);

   User updateUser(String uuid, User updatedUser);

   void deleteUser(String userUuid);

   List<User> getAllUsers();
}
