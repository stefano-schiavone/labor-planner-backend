package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.AccountType;
import com.laborplanner.backend.repository.entity.AccountTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper extends BaseMapper<AccountType, AccountTypeEntity> {

   AccountType toModel(AccountTypeEntity entity);

   AccountTypeEntity toEntity(AccountType model);
}
