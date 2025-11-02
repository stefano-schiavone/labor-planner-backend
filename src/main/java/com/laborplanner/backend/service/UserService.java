package com.laborplanner.backend.service;

import com.laborplanner.backend.exception.user.UserNotFoundException;
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
  // @Override
  public User createUser(User user) {
    return userRepository.create(user);
  }

  // @Override
  public User getUserByUuid(String userUuid) {
    return userRepository
        .findByUuid(userUuid)
        .orElseThrow(() -> new UserNotFoundException(userUuid));
  }

  // @Override
  public User updateUser(String uuid, User updatedUser) {
    // Find existing user by UUID
    User existingUser =
        userRepository.findByUuid(uuid).orElseThrow(() -> new UserNotFoundException(uuid));

    // Update fields
    existingUser.setName(updatedUser.getName());
    existingUser.setLastName(updatedUser.getLastName());
    existingUser.setEmail(updatedUser.getEmail());
    existingUser.setType(updatedUser.getType());

    // Save and return
    return userRepository.update(existingUser);
  }

  // @Override
  public void deleteUser(String userUuid) {
    if (!userRepository.existsByUuid(userUuid)) {
      throw new RuntimeException("User not found with UUID: " + userUuid);
    }
    userRepository.deleteByUuid(userUuid);
  }

  // @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  // ---------------------------
  // IUserAuthService Implementation
  // ---------------------------
  // @Override
  public User getUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }

  // Note: createUser() and updateUser() already implemented in section for IUserService

  // ---------------------------
  // IUserTokenService Implementation
  // ---------------------------
  // Note: getUserByUuid() already implemented in IUserService
}
