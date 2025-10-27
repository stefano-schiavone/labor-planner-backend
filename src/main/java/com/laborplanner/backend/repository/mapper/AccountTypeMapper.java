package com.laborplanner.backend.mapper;

import com.laborplanner.backend.model.AccountType;
import com.laborplanner.backend.repository.entity.AccountTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper {

  AccountTypeMapper INSTANCE = Mappers.getMapper(AccountTypeMapper.class);

  AccountType toModel(AccountTypeEntity entity);

  AccountTypeEntity toEntity(AccountType model);
}
