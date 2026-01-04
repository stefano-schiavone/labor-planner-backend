package com.laborplanner.backend.controller;

import com.laborplanner.backend.dto.user.CreateUserRequest;
import com.laborplanner.backend.dto.user.UserResponse;
import com.laborplanner.backend.model.User;
import com.laborplanner.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Manage application users")
public class UserController {

   @Autowired
   private UserService userService;

   @Operation(summary = "Get all users")
   @GetMapping
   public List<User> getAllUsers() {
      return userService.getAllUsers();
   }

   @Operation(summary = "Get a user by UUID")
   @GetMapping("/{uuid}")
   public User getUser(@PathVariable String uuid) {
      return userService.getUserByUuid(uuid);
   }

   @Operation(summary = "Create a new user")
   @PostMapping
   public UserResponse createUser(@RequestBody CreateUserRequest request) {
      User createdUser = userService.createUser(request);

      // Map User -> UserResponse
      return new UserResponse(
            createdUser.getUserUuid(),
            createdUser.getName(),
            createdUser.getLastName(),
            createdUser.getEmail(),
            createdUser.getType());
   }

   @Operation(summary = "Update a user")
   @PutMapping("/{uuid}")
   public User updateUser(@PathVariable String uuid, @RequestBody User updatedUser) {
      return userService.updateUser(uuid, updatedUser);
   }

   @Operation(summary = "Delete a user")
   @DeleteMapping("/{uuid}")
   public void deleteUser(@PathVariable String uuid) {
      userService.deleteUser(uuid);
   }
}
