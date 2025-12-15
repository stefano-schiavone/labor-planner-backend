package com.laborplanner.backend.repository;

import com.laborplanner.backend.repository.custom.BaseRepositoryCustom;
import com.laborplanner.backend.repository.mapper.BaseMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseRepository<E, M, MAPPER extends BaseMapper<M, E>>
      implements BaseRepositoryCustom<E, M, MAPPER> {

   @PersistenceContext
   protected EntityManager em;

   private final Class<E> entityClass;
   protected final MAPPER mapper;

   protected BaseRepository(Class<E> entityClass, MAPPER mapper) {
      this.entityClass = entityClass;
      this.mapper = mapper;
   }

   public M create(M model) {
      E entity = mapper.toEntity(model);
      em.persist(entity);
      em.flush();
      return mapper.toModel(entity);
   }

   public M update(M model) {
      E entity = mapper.toEntity(model);
      entity = em.merge(entity);
      em.flush();
      return mapper.toModel(entity);
   }

   public M save(M model) {
      E entity = mapper.toEntity(model);
      entity = em.merge(entity);
      em.flush();
      return mapper.toModel(entity);
   }

   @Override
   public Optional<M> findByUuid(String uuid) {
      UUID parsed = UUID.fromString(uuid);
      E entity = em.find(entityClass, parsed);
      return Optional.ofNullable(entity).map(mapper::toModel);
   }

   @Override
   public List<M> findAll() {
      return em
            .createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
            .getResultList()
            .stream()
            .map(mapper::toModel)
            .toList();
   }

   @Override
   public void deleteByUuid(String uuid) {
      UUID parsed = UUID.fromString(uuid);
      E entity = em.find(entityClass, parsed);
      if (entity != null) {
         em.remove(entity);
      }
   }

   @Override
   public boolean existsByUuid(String uuid) {
      UUID parsed = UUID.fromString(uuid);
      return em.find(entityClass, parsed) != null;
   }
}
