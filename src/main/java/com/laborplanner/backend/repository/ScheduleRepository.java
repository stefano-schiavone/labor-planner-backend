package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.custom.ScheduleRepositoryCustom;
import com.laborplanner.backend.repository.entity.ScheduleEntity;
import com.laborplanner.backend.repository.mapper.ScheduleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ScheduleRepository extends BaseRepository<ScheduleEntity, Schedule, ScheduleMapper>
      implements ScheduleRepositoryCustom {

   @PersistenceContext
   private EntityManager em;

   public ScheduleRepository(ScheduleMapper mapper) {
      super(ScheduleEntity.class, mapper);
   }

   @Override
   public List<Schedule> findByCreatedByUser(User user) {
      TypedQuery<ScheduleEntity> query = em.createQuery(
            "SELECT s FROM ScheduleEntity s WHERE s.createdByUser = :user",
            ScheduleEntity.class);
      query.setParameter("user", user);
      return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
   }

   @Override
   public Optional<Schedule> findByWeekStartDate(LocalDateTime weekStartDate) {
      TypedQuery<ScheduleEntity> query = em.createQuery(
            "SELECT s FROM ScheduleEntity s WHERE s.weekStartDate = :weekStartDate",
            ScheduleEntity.class);
      query.setParameter("weekStartDate", weekStartDate);
      return query.getResultStream().findFirst().map(mapper::toModel);
   }

   @Override
   public boolean existsByWeekStartDate(LocalDateTime weekStartDate) {
      TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(s) FROM ScheduleEntity s WHERE s.weekStartDate = :weekStartDate",
            Long.class);
      query.setParameter("weekStartDate", weekStartDate);
      Long count = query.getSingleResult();
      return count != null && count > 0;
   }

   @Override
   public List<Schedule> findByWeekStartDateBetween(LocalDateTime start, LocalDateTime end) {
      TypedQuery<ScheduleEntity> query = em.createQuery(
            "SELECT s FROM ScheduleEntity s WHERE s.weekStartDate BETWEEN :start AND :end",
            ScheduleEntity.class);
      query.setParameter("start", start);
      query.setParameter("end", end);
      return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
   }

   @Override
   public Optional<Schedule> findFirstByCreatedByUserOrderByLastModifiedDateDesc(User user) {
      TypedQuery<ScheduleEntity> query = em.createQuery(
            "SELECT s FROM ScheduleEntity s WHERE s.createdByUser = :user ORDER BY s.lastModifiedDate DESC",
            ScheduleEntity.class);
      query.setParameter("user", user);
      query.setMaxResults(1);
      return query.getResultStream().findFirst().map(mapper::toModel);
   }

   @Override
   public Optional<Schedule> findScheduleContainingJob(String jobUuid) {
      try {
         // Query through ScheduledJobEntity instead
         TypedQuery<ScheduleEntity> query = em.createQuery(
               "SELECT DISTINCT sj. schedule FROM ScheduledJobEntity sj " +
                     "WHERE sj. job.jobUuid = :jobUuid",
               ScheduleEntity.class);
         query.setParameter("jobUuid", UUID.fromString(jobUuid));
         return query.getResultStream().findFirst().map(mapper::toModel);
      } catch (Exception e) {
         log.error("Error finding schedule containing job {}: {}", jobUuid, e.getMessage());
         return Optional.empty();
      }
   }
}
