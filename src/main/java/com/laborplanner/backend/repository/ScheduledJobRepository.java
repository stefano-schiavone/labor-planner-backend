package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.ScheduledJob;
import com.laborplanner.backend.repository.custom.ScheduledJobRepositoryCustom;
import com.laborplanner.backend.repository.entity.ScheduledJobEntity;
import com.laborplanner.backend.repository.mapper.ScheduledJobMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduledJobRepository
    extends BaseRepository<ScheduledJobEntity, ScheduledJob, ScheduledJobMapper>
    implements ScheduledJobRepositoryCustom {

  @PersistenceContext private EntityManager em;

  public ScheduledJobRepository(ScheduledJobMapper mapper) {
    super(ScheduledJobEntity.class, mapper);
  }

  @Override
  public List<ScheduledJob> findBySchedule(Schedule schedule) {
    TypedQuery<ScheduledJobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduledJobEntity.class);
    query.setParameter("schedule", schedule);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<ScheduledJob> findByMachine(Machine machine) {
    TypedQuery<ScheduledJobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduledJobEntity.class);
    query.setParameter("machine", machine);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<ScheduledJob> findByJob(Job job) {
    TypedQuery<ScheduledJobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduledJobEntity.class);
    query.setParameter("job", job);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public boolean existsByMachineAndStartTimeBeforeAndEndTimeAfter(
      Machine machine, LocalDateTime end, LocalDateTime start) {
    TypedQuery<Long> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            Long.class);
    query.setParameter("machine", machine);
    query.setParameter("end", end);
    query.setParameter("start", start);
    Long count = query.getSingleResult();
    return count != null && count > 0;
  }

  @Override
  public List<ScheduledJob> findByStartTimeBetween(LocalDateTime start, LocalDateTime end) {
    TypedQuery<ScheduledJobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            ScheduledJobEntity.class);
    query.setParameter("start", start);
    query.setParameter("end", end);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
