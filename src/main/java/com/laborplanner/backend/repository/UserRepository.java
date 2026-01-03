package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.custom.UserRepositoryCustom;
import com.laborplanner.backend.repository.entity.UserEntity;
import com.laborplanner.backend.repository.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends BaseRepository<UserEntity, User, UserMapper>
      implements UserRepositoryCustom {

   @PersistenceContext
   private EntityManager em;

   public UserRepository(UserMapper mapper) {
      super(UserEntity.class, mapper);
   }

   @Transactional()
   @Override
   public Optional<User> findByEmail(String email) {
      TypedQuery<UserEntity> query = em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :email",
            UserEntity.class);
      query.setParameter("email", email);
      return query.getResultStream().findFirst().map(mapper::toModel);
   }

   @Override
   public boolean existsByEmail(String email) {
      TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class);
      query.setParameter("email", email);
      Long count = query.getSingleResult();
      return count != null && count > 0;
   }
}
