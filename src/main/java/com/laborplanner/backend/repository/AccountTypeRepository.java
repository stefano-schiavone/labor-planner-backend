package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.AccountType;
import com.laborplanner.backend.repository.custom.AccountTypeRepositoryCustom;
import com.laborplanner.backend.repository.entity.AccountTypeEntity;
import com.laborplanner.backend.repository.mapper.AccountTypeMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AccountTypeRepository
      extends BaseRepository<AccountTypeEntity, AccountType, AccountTypeMapper>
      implements AccountTypeRepositoryCustom {

   @PersistenceContext
   private EntityManager em;

   public AccountTypeRepository(AccountTypeMapper mapper) {
      super(AccountTypeEntity.class, mapper);
   }

   @Override
   public Optional<AccountType> findByName(String name) {
      TypedQuery<AccountTypeEntity> query = em.createQuery(
            "SELECT a FROM AccountTypeEntity a WHERE a.name = :name", AccountTypeEntity.class);
      query.setParameter("name", name);
      return query.getResultStream().findFirst().map(mapper::toModel);
   }

   @Override
   public boolean existsByName(String name) {
      TypedQuery<Long> query = em.createQuery("SELECT COUNT(a) FROM AccountTypeEntity a WHERE a.name = :name",
            Long.class);
      query.setParameter("name", name);
      Long count = query.getSingleResult();
      return count != null && count > 0;
   }

   @Override
   public List<AccountType> findAllByOrderByNameAsc() {
      TypedQuery<AccountTypeEntity> query = em.createQuery(
            "SELECT a FROM AccountTypeEntity a ORDER BY a.name ASC", AccountTypeEntity.class);
      return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
   }
}
