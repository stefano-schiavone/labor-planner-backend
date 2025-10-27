package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.repository.custom.MachineStatusRepositoryCustom;
import com.laborplanner.backend.repository.entity.MachineStatusEntity;
import com.laborplanner.backend.repository.mapper.MachineStatusMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MachineStatusRepository
    extends BaseRepository<MachineStatusEntity, String, MachineStatus, MachineStatusMapper>
    implements MachineStatusRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final MachineStatusMapper mapper = MachineStatusMapper.INSTANCE;

  public MachineStatusRepository() {
    super(MachineStatusEntity.class, MachineStatusMapper.INSTANCE);
  }

  @Override
  public Optional<MachineStatus> findByName(String name) {
    TypedQuery<MachineStatusEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineStatusEntity.class);
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
  public List<MachineStatus> findAllByOrderByNameAsc() {
    TypedQuery<MachineStatusEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineStatusEntity.class);
    List<MachineStatusEntity> entities = query.getResultList();
    return entities.stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
