package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.AccountType;
import java.util.List;
import java.util.Optional;

public interface IAccountTypeService {
  List<AccountType> getAllAccountTypes();

  AccountType getAccountTypeByUuid(String uuid);

  AccountType createAccountType(AccountType accountType);

  AccountType updateAccountType(String uuid, AccountType updatedAccountType);

  void deleteAccountType(String uuid);

  Optional<AccountType> findByName(String name);
}
