package com.laborplanner.backend.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.exception.user.UserNotFoundException;
import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.UserRepository;
import com.laborplanner.backend.service.UserService;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

   @Mock
   private UserRepository userRepository;

   @InjectMocks
   private UserService userService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   // ----------------------------
   // Create user
   // ----------------------------

   @Test
   void createUser_savesAndReturnsCreatedUser() {
      User user = new User();
      user.setName("John");

      when(userRepository.create(user)).thenReturn(user);

      User created = userService.createUser(user);

      assertEquals("John", created.getName());
      verify(userRepository).create(user);
   }

   // ----------------------------
   // Get user by UUID
   // ----------------------------

   @Test
   void getUserByUuid_exists_returnsUser() {
      User u = new User();
      u.setUserUuid("u1");

      when(userRepository.findByUuid("u1")).thenReturn(Optional.of(u));

      User found = userService.getUserByUuid("u1");

      assertEquals("u1", found.getUserUuid());
   }

   @Test
   void getUserByUuid_missing_throwsException() {
      when(userRepository.findByUuid("missing")).thenReturn(Optional.empty());
      assertThrows(UserNotFoundException.class, () -> userService.getUserByUuid("missing"));
   }

   // ----------------------------
   // Update user
   // ----------------------------

   @Test
   void updateUser_found_updatesFieldsAndReturnsSaved() {
      User existing = new User();
      existing.setUserUuid("u1");
      existing.setName("Old");

      User updated = new User();
      updated.setName("New");

      when(userRepository.findByUuid("u1")).thenReturn(Optional.of(existing));
      when(userRepository.update(any(User.class)))
            .thenAnswer(i -> i.getArgument(0));

      User result = userService.updateUser("u1", updated);

      assertEquals("New", result.getName());
      verify(userRepository).update(existing);
   }

   @Test
   void updateUser_missing_throwsException() {
      User updated = new User();
      when(userRepository.findByUuid("nope")).thenReturn(Optional.empty());
      assertThrows(UserNotFoundException.class, () -> userService.updateUser("nope", updated));
   }

   // ----------------------------
   // Delete user
   // ----------------------------

   @Test
   void deleteUser_exists_deletesSuccessfully() {
      when(userRepository.existsByUuid("u1")).thenReturn(true);
      doNothing().when(userRepository).deleteByUuid("u1");

      userService.deleteUser("u1");

      verify(userRepository).deleteByUuid("u1");
   }

   @Test
   void deleteUser_missing_throwsRuntimeException() {
      when(userRepository.existsByUuid("x")).thenReturn(false);

      assertThrows(RuntimeException.class, () -> userService.deleteUser("x"));
      verify(userRepository, never()).deleteByUuid(anyString());
   }

   // ----------------------------
   // Authentication / email lookup
   // ----------------------------

   @Test
   void getUserByEmail_exists_returnsUser() {
      User u = new User();
      u.setEmail("john@example.com");

      when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(u));

      User found = userService.getUserByEmail("john@example.com");

      assertEquals("john@example.com", found.getEmail());
   }

   @Test
   void getUserByEmail_missing_throwsRuntimeException() {
      when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());
      assertThrows(RuntimeException.class, () -> userService.getUserByEmail("missing@mail.com"));
   }

   // ----------------------------
   // Get all
   // ----------------------------

   @Test
   void getAllUsers_returnsList() {
      User u1 = new User();
      User u2 = new User();

      when(userRepository.findAll()).thenReturn(List.of(u1, u2));

      List<User> list = userService.getAllUsers();

      assertEquals(2, list.size());
   }
}
