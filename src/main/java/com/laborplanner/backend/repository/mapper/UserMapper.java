package com.laborplanner.backend.repository.mapper;

import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { AccountTypeMapper.class })
public interface UserMapper extends BaseMapper<User, UserEntity> {

   @Mapping(source = "type", target = "type")
   User toModel(UserEntity entity);

   @Mapping(source = "type", target = "type")
   UserEntity toEntity(User model);
}
