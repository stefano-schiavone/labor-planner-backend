package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.custom.UserRepositoryCustom;
import com.laborplanner.backend.repository.entity.UserEntity;
import com.laborplanner.backend.repository.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements UserRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final UserMapper mapper = UserMapper.INSTANCE;

  @Override
  public Optional<User> findByEmail(String email) {
    TypedQuery<UserEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            UserEntity.class);
    query.setParameter("email", email);
    return query.getResultStream().findFirst().map(mapper::toModel);
  }

  @Override
  public boolean existsByEmail(String email) {
    TypedQuery<Long> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            Long.class);
    query.setParameter("email", email);
    Long count = query.getSingleResult();
    return count != null && count > 0;
  }
}
