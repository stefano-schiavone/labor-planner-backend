package com.laborplanner.backend.service;

import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.UserRepository;
import com.laborplanner.backend.service.interfaces.*;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService, IUserAuthService, IUserTokenService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // ---------------------------
  // IUserService Implementation
  // ---------------------------
  @Override
  public User createUser(User user) {
    return userRepository.save(user);
  }

  @Override
  public User getUserByUuid(String userUuid) {
    return userRepository
        .findById(userUuid)
        .orElseThrow(() -> new RuntimeException("User not found with UUID: " + userUuid));
  }

  @Override
  public User updateUser(User user) {
    String uuid = user.getUserUuid();
    User existingUser =
        userRepository
            .findById(uuid)
            .orElseThrow(() -> new RuntimeException("User not found with UUID: " + uuid));

    existingUser.setName(user.getName());
    existingUser.setLastName(user.getLastName());
    existingUser.setEmail(user.getEmail());
    existingUser.setType(user.getType());

    return userRepository.save(existingUser);
  }

  @Override
  public void deleteUser(String userUuid) {
    if (!userRepository.existsById(userUuid)) {
      throw new RuntimeException("User not found with UUID: " + userUuid);
    }
    userRepository.deleteById(userUuid);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  // ---------------------------
  // IUserAuthService Implementation
  // ---------------------------
  @Override
  public User getUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }

  // Note: createUser() and updateUser() already implemented in IUserService

  // ---------------------------
  // IUserTokenService Implementation
  // ---------------------------
  // Note: getUserByUuid() already implemented in IUserService
}
