package com.laborplanner.backend.controller;

import com.laborplanner.backend.model.AccountType;
import com.laborplanner.backend.service.AccountTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account-types")
@Tag(name = "Account Types", description = "Manage account type definitions")
public class AccountTypeController {

  @Autowired private AccountTypeService accountTypeService;

  @Operation(summary = "Get all account types")
  @GetMapping
  public List<AccountType> getAllAccountTypes() {
    return accountTypeService.getAllAccountTypes();
  }

  @Operation(summary = "Get account type by UUID")
  @GetMapping("/{uuid}")
  public AccountType getAccountType(@PathVariable String uuid) {
    return accountTypeService.getAccountTypeByUuid(uuid);
  }

  @Operation(summary = "Create a new account type")
  @PostMapping
  public AccountType createAccountType(@RequestBody AccountType accountType) {
    return accountTypeService.createAccountType(accountType);
  }

  @Operation(summary = "Update an existing account type")
  @PutMapping("/{uuid}")
  public AccountType updateAccountType(
      @PathVariable String uuid, @RequestBody AccountType updatedAccountType) {
    return accountTypeService.updateAccountType(uuid, updatedAccountType);
  }

  @Operation(summary = "Delete an account type")
  @DeleteMapping("/{uuid}")
  public void deleteAccountType(@PathVariable String uuid) {
    accountTypeService.deleteAccountType(uuid);
  }
}
