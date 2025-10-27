package com.laborplanner.backend.repository;

import com.laborplanner.backend.repository.custom.BaseRepositoryCustom;
import com.laborplanner.backend.repository.mapper.BaseMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<E, UUID, M, MAPPER extends BaseMapper<M, E>>
    implements BaseRepositoryCustom<E, UUID, M, MAPPER> {

  @PersistenceContext protected EntityManager em;

  private final Class<E> entityClass;
  protected final MAPPER mapper;

  protected BaseRepository(Class<E> entityClass, MAPPER mapper) {
    this.entityClass = entityClass;
    this.mapper = mapper;
  }

  public M save(M model) {
    E entity = mapper.toEntity(model);
    if (!em.contains(entity)) {
      entity = em.merge(entity);
    } else {
      em.persist(entity);
    }
    em.flush();
    return mapper.toModel(entity);
  }

  @Override
  public Optional<M> findByUuid(UUID uuid) {
    E entity = em.find(entityClass, uuid);
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
  public void deleteByUuid(UUID uuid) {
    E entity = em.find(entityClass, uuid);
    if (entity != null) {
      em.remove(entity);
    }
  }

  @Override
  public boolean existsByUuid(UUID uuid) {
    return em.find(entityClass, uuid) != null;
  }
}
