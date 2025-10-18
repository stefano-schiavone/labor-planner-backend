package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.User;

public interface IUserAuthService {
  User getUserByEmail(String email);

  User createUser(User user);

  User updateUser(User user);
}
