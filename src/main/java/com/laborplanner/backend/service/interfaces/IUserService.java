package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.User;
import java.util.List;

public interface IUserService {
  User createUser(User user);

  User getUserByUuid(String userUuid);

  User updateUser(User user);

  void deleteUser(String userUuid);

  List<User> getAllUsers();
}
