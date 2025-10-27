package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.custom.MachineTypeRepositoryCustom;
import com.laborplanner.backend.repository.entity.MachineTypeEntity;
import com.laborplanner.backend.repository.mapper.MachineTypeMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MachineTypeRepository
    extends BaseRepository<MachineTypeEntity, String, MachineType, MachineTypeMapper>
    implements MachineTypeRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final MachineTypeMapper mapper = MachineTypeMapper.INSTANCE;

  public MachineTypeRepository() {
    super(MachineTypeEntity.class, MachineTypeMapper.INSTANCE);
  }

  @Override
  public Optional<MachineType> findByName(String name) {
    TypedQuery<MachineTypeEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineTypeEntity.class);
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
  public List<MachineType> findAllByOrderByNameAsc() {
    TypedQuery<MachineTypeEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            MachineTypeEntity.class);
    List<MachineTypeEntity> entities = query.getResultList();
    return entities.stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
