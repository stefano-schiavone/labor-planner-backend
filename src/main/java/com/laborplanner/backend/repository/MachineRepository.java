package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.custom.MachineRepositoryCustom;
import com.laborplanner.backend.repository.entity.MachineEntity;
import com.laborplanner.backend.repository.mapper.MachineMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MachineRepository implements MachineRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final MachineMapper mapper = MachineMapper.INSTANCE;

  @Override
  public Optional<Machine> findByName(String name) {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineEntity.class);
    query.setParameter("name", name);
    return query.getResultStream().findFirst().map(mapper::toModel);
  }

  @Override
  public boolean existsByName(String name) {
    TypedQuery<Long> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            Long.class);
    query.setParameter("name", name);
    Long count = query.getSingleResult();
    return count != null && count > 0;
  }

  @Override
  public List<Machine> findByType(MachineType type) {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineEntity.class);
    query.setParameter("type", type);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Machine> findByStatus(MachineStatus status) {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineEntity.class);
    query.setParameter("status", status);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Machine> findByTypeAndStatus(MachineType type, MachineStatus status) {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineEntity.class);
    query.setParameter("type", type);
    query.setParameter("status", status);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Machine> findAllByOrderByNameAsc() {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineEntity.class);
    List<MachineEntity> entities = query.getResultList();
    return entities.stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
