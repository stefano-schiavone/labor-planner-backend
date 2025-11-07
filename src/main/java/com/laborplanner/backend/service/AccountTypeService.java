package com.laborplanner.backend.service;

import com.laborplanner.backend.exception.user.AccountTypeNotFoundException;
import com.laborplanner.backend.exception.user.DuplicateAccountTypeNameException;
import com.laborplanner.backend.model.AccountType;
import com.laborplanner.backend.repository.AccountTypeRepository;
import com.laborplanner.backend.service.interfaces.IAccountTypeService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class AccountTypeService implements IAccountTypeService {

  private final AccountTypeRepository accountTypeRepository;

  public AccountTypeService(AccountTypeRepository accountTypeRepository) {
    this.accountTypeRepository = accountTypeRepository;
  }

  @Override
  public List<AccountType> getAllAccountTypes() {
    return accountTypeRepository.findAllByOrderByNameAsc();
  }

  @Override
  public AccountType getAccountTypeByUuid(String uuid) {
    return accountTypeRepository
        .findByUuid(uuid)
        .orElseThrow(
            () -> {
              log.warn("AccountType not found: {}", uuid);
              return new AccountTypeNotFoundException(uuid);
            });
  }

  @Override
  public AccountType createAccountType(AccountType accountType) {
    log.info("Creating AccountType: name='{}'", accountType.getName());

    if (accountTypeRepository.existsByName(accountType.getName())) {
      log.warn("Duplicate AccountType name: {}", accountType.getName());
      throw new DuplicateAccountTypeNameException(accountType.getName());
    }

    AccountType created = accountTypeRepository.create(accountType);
    log.info("AccountType created successfully: uuid='{}'", created.getAccountTypeUuid());
    return created;
  }

  @Override
  public AccountType updateAccountType(String uuid, AccountType updated) {
    AccountType existing =
        accountTypeRepository
            .findByUuid(uuid)
            .orElseThrow(
                () -> {
                  log.warn("AccountType not found for update: {}", uuid);
                  return new AccountTypeNotFoundException(uuid);
                });

    existing.setName(updated.getName());
    AccountType saved = accountTypeRepository.update(existing);
    log.info("AccountType updated successfully: uuid='{}'", saved.getAccountTypeUuid());
    return saved;
  }

  @Override
  public void deleteAccountType(String uuid) {
    if (!accountTypeRepository.existsByUuid(uuid)) {
      throw new AccountTypeNotFoundException(uuid);
    }
    accountTypeRepository.deleteByUuid(uuid);
    log.info("Deleted AccountType: uuid='{}'", uuid);
  }

  @Override
  public Optional<AccountType> findByName(String name) {
    return accountTypeRepository.findByName(name);
  }
}
