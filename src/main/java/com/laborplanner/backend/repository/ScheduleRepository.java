package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.custom.ScheduleRepositoryCustom;
import com.laborplanner.backend.repository.entity.ScheduleEntity;
import com.laborplanner.backend.repository.mapper.ScheduleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleRepository extends BaseRepository<ScheduleEntity, Schedule, ScheduleMapper>
    implements ScheduleRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final ScheduleMapper mapper = ScheduleMapper.INSTANCE;

  public ScheduleRepository() {
    super(ScheduleEntity.class, ScheduleMapper.INSTANCE);
  }

  @Override
  public List<Schedule> findByCreatedByUser(User user) {
    TypedQuery<ScheduleEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduleEntity.class);
    query.setParameter("user", user);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public Optional<Schedule> findByWeekStartDate(LocalDateTime weekStartDate) {
    TypedQuery<ScheduleEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduleEntity.class);
    query.setParameter("weekStartDate", weekStartDate);
    return query.getResultStream().findFirst().map(mapper::toModel);
  }

  @Override
  public boolean existsByWeekStartDate(LocalDateTime weekStartDate) {
    TypedQuery<Long> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            Long.class);
    query.setParameter("weekStartDate", weekStartDate);
    Long count = query.getSingleResult();
    return count != null && count > 0;
  }

  @Override
  public List<Schedule> findByWeekStartDateBetween(LocalDateTime start, LocalDateTime end) {
    TypedQuery<ScheduleEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduleEntity.class);
    query.setParameter("start", start);
    query.setParameter("end", end);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public Optional<Schedule> findFirstByCreatedByUserOrderByLastModifiedDateDesc(User user) {
    TypedQuery<ScheduleEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduleEntity.class);
    query.setParameter("user", user);
    query.setMaxResults(1); // get only the latest modified
    return query.getResultStream().findFirst().map(mapper::toModel);
  }
}
