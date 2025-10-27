package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.AccountType;
import com.laborplanner.backend.repository.entity.AccountTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper extends BaseMapper<AccountType, AccountTypeEntity> {

  AccountTypeMapper INSTANCE = Mappers.getMapper(AccountTypeMapper.class);

  AccountType toModel(AccountTypeEntity entity);

  AccountTypeEntity toEntity(AccountType model);
}
