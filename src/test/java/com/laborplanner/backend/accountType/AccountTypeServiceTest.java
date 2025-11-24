package com.laborplanner.backend.accountType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.exception.user.AccountTypeNotFoundException;
import com.laborplanner.backend.exception.user.DuplicateAccountTypeNameException;
import com.laborplanner.backend.model.AccountType;
import com.laborplanner.backend.repository.AccountTypeRepository;
import com.laborplanner.backend.service.AccountTypeService;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountTypeServiceTest {

   @Mock
   private AccountTypeRepository accountTypeRepository;

   @InjectMocks
   private AccountTypeService accountTypeService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   // ----------------------------
   // Get and list tests
   // ----------------------------

   @Test
   void getAllAccountTypes_returnsList() {
      AccountType a1 = new AccountType();
      a1.setAccountTypeUuid("a1");
      a1.setName("Name1");

      AccountType a2 = new AccountType();
      a2.setAccountTypeUuid("a2");
      a2.setName("Name2");

      when(accountTypeRepository.findAllByOrderByNameAsc()).thenReturn(List.of(a1, a2));

      List<AccountType> result = accountTypeService.getAllAccountTypes();

      assertEquals(2, result.size());
      assertEquals("Name1", result.get(0).getName());
   }

   @Test
   void getAccountTypeByUuid_whenExists_returnsEntity() {
      AccountType a = new AccountType();
      a.setAccountTypeUuid("id123");
      a.setName("Test");

      when(accountTypeRepository.findByUuid("id123")).thenReturn(Optional.of(a));

      AccountType result = accountTypeService.getAccountTypeByUuid("id123");

      assertEquals("id123", result.getAccountTypeUuid());
      assertEquals("Test", result.getName());
   }

   @Test
   void getAccountTypeByUuid_whenMissing_throwsException() {
      when(accountTypeRepository.findByUuid("missing")).thenReturn(Optional.empty());
      assertThrows(AccountTypeNotFoundException.class, () -> accountTypeService.getAccountTypeByUuid("missing"));
   }

   // ----------------------------
   // Create tests
   // ----------------------------

   @Test
   void createAccountType_withDuplicateName_throwsException() {
      AccountType type = new AccountType();
      type.setName("Dup");

      when(accountTypeRepository.existsByName("Dup")).thenReturn(true);

      assertThrows(DuplicateAccountTypeNameException.class, () -> accountTypeService.createAccountType(type));
      verify(accountTypeRepository, never()).create(any());
   }

   @Test
   void createAccountType_success_returnsCreated() {
      AccountType type = new AccountType();
      type.setName("Good");

      when(accountTypeRepository.existsByName("Good")).thenReturn(false);

      when(accountTypeRepository.create(any(AccountType.class)))
            .thenAnswer(i -> {
               AccountType t = i.getArgument(0);
               t.setAccountTypeUuid("uuid-1");
               return t;
            });

      AccountType created = accountTypeService.createAccountType(type);

      assertEquals("uuid-1", created.getAccountTypeUuid());
      assertEquals("Good", created.getName());
   }

   // ----------------------------
   // Update tests
   // ----------------------------

   @Test
   void updateAccountType_whenExists_updatesSuccessfully() {
      AccountType existing = new AccountType();
      existing.setAccountTypeUuid("id");
      existing.setName("Old");

      AccountType updated = new AccountType();
      updated.setName("New");

      when(accountTypeRepository.findByUuid("id")).thenReturn(Optional.of(existing));
      when(accountTypeRepository.update(any(AccountType.class)))
            .thenAnswer(i -> i.getArgument(0));

      AccountType saved = accountTypeService.updateAccountType("id", updated);

      assertEquals("id", saved.getAccountTypeUuid());
      assertEquals("New", saved.getName());
   }

   @Test
   void updateAccountType_whenMissing_throwsException() {
      AccountType updated = new AccountType();
      updated.setName("X");

      when(accountTypeRepository.findByUuid("missing")).thenReturn(Optional.empty());

      assertThrows(AccountTypeNotFoundException.class, () -> accountTypeService.updateAccountType("missing", updated));
   }

   // ----------------------------
   // Delete tests
   // ----------------------------

   @Test
   void deleteAccountType_whenExists_deletesSuccessfully() {
      when(accountTypeRepository.existsByUuid("del")).thenReturn(true);
      doNothing().when(accountTypeRepository).deleteByUuid("del");

      accountTypeService.deleteAccountType("del");

      verify(accountTypeRepository, times(1)).deleteByUuid("del");
   }

   @Test
   void deleteAccountType_whenMissing_throwsException() {
      when(accountTypeRepository.existsByUuid("nope")).thenReturn(false);

      assertThrows(AccountTypeNotFoundException.class, () -> accountTypeService.deleteAccountType("nope"));
      verify(accountTypeRepository, never()).deleteByUuid(any());
   }

   // ----------------------------
   // Find by name tests
   // ----------------------------

   @Test
   void findByName_returnsOptional() {
      AccountType t = new AccountType();
      t.setName("Search");

      when(accountTypeRepository.findByName("Search")).thenReturn(Optional.of(t));

      Optional<AccountType> result = accountTypeService.findByName("Search");

      assertTrue(result.isPresent());
      assertEquals("Search", result.get().getName());
   }
}
