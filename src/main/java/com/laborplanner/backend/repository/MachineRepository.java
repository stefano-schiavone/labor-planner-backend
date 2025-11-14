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
public class MachineRepository extends BaseRepository<MachineEntity, Machine, MachineMapper>
    implements MachineRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final MachineMapper mapper;

  public MachineRepository(MachineMapper mapper) {
    super(MachineEntity.class, mapper);
    this.mapper = mapper;
  }

  @Override
  public Optional<Machine> findByName(String name) {
    TypedQuery<MachineEntity> query =
        em.createQuery("SELECT m FROM MachineEntity m WHERE m.name = :name", MachineEntity.class);
    query.setParameter("name", name);
    return query.getResultStream().findFirst().map(mapper::toModel);
  }

  @Override
  public boolean existsByName(String name) {
    TypedQuery<Long> query =
        em.createQuery("SELECT COUNT(m) FROM MachineEntity m WHERE m.name = :name", Long.class);
    query.setParameter("name", name);
    Long count = query.getSingleResult();
    return count != null && count > 0;
  }

  @Override
  public List<Machine> findByType(MachineType type) {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "SELECT m FROM MachineEntity m WHERE m.type.name = :typeName", MachineEntity.class);
    query.setParameter("typeName", type.getName());
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Machine> findByStatus(MachineStatus status) {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "SELECT m FROM MachineEntity m WHERE m.status.name = :statusName", MachineEntity.class);
    query.setParameter("statusName", status.getName());
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Machine> findByTypeAndStatus(MachineType type, MachineStatus status) {
    TypedQuery<MachineEntity> query =
        em.createQuery(
            "SELECT m FROM MachineEntity m WHERE m.type.name = :typeName AND m.status.name ="
                + " :statusName",
            MachineEntity.class);
    query.setParameter("typeName", type.getName());
    query.setParameter("statusName", status.getName());
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Machine> findAllByOrderByNameAsc() {
    TypedQuery<MachineEntity> query =
        em.createQuery("SELECT m FROM MachineEntity m ORDER BY m.name ASC", MachineEntity.class);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
