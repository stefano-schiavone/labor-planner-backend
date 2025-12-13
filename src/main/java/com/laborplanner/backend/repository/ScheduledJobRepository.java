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
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduledJobRepository
      extends BaseRepository<ScheduledJobEntity, ScheduledJob, ScheduledJobMapper>
      implements ScheduledJobRepositoryCustom {

   @PersistenceContext
   private EntityManager em;

   public ScheduledJobRepository(ScheduledJobMapper mapper) {
      super(ScheduledJobEntity.class, mapper);
   }

   @Override
   public List<ScheduledJob> findBySchedule(Schedule schedule) {
      TypedQuery<ScheduledJobEntity> query = em.createQuery(
            "SELECT sj FROM ScheduledJobEntity sj WHERE sj.schedule.scheduleUuid = :scheduleUuid",
            ScheduledJobEntity.class);
      query.setParameter("scheduleUuid", UUID.fromString(schedule.getScheduleUuid()));
      return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
   }

   @Override
   public List<ScheduledJob> findByMachine(Machine machine) {
      TypedQuery<ScheduledJobEntity> query = em.createQuery(
            "SELECT sj FROM ScheduledJobEntity sj WHERE sj.machine.machineUuid = :machineUuid",
            ScheduledJobEntity.class);
      query.setParameter("machineUuid", UUID.fromString(machine.getMachineUuid()));
      return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
   }

   @Override
   public List<ScheduledJob> findByJob(Job job) {
      TypedQuery<ScheduledJobEntity> query = em.createQuery(
            "SELECT sj FROM ScheduledJobEntity sj WHERE sj.job.jobUuid = :jobUuid",
            ScheduledJobEntity.class);
      query.setParameter("jobUuid", UUID.fromString(job.getJobUuid()));
      return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
   }

   @Override
   public boolean existsByMachineAndStartTimeBeforeAndEndTimeAfter(
         Machine machine, LocalDateTime end, LocalDateTime start) {
      TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(sj) FROM ScheduledJobEntity sj " +
                  "WHERE sj.machine.machineUuid = :machineUuid " +
                  "AND sj.startingTimeGrainIndex < :endIndex " +
                  "AND (sj.startingTimeGrainIndex + sj.job.durationGrains) > :startIndex",
            Long.class);
      query.setParameter("machineUuid", UUID.fromString(machine.getMachineUuid()));
      query.setParameter("endIndex", end); // adjust depending on how you map TimeGrain → LocalDateTime
      query.setParameter("startIndex", start);
      Long count = query.getSingleResult();
      return count != null && count > 0;
   }

   @Override
   public List<ScheduledJob> findByStartTimeBetween(LocalDateTime start, LocalDateTime end) {
      TypedQuery<ScheduledJobEntity> query = em.createQuery(
            "SELECT sj FROM ScheduledJobEntity sj WHERE sj.startingTimeGrainIndex BETWEEN :startIndex AND :endIndex",
            ScheduledJobEntity.class);
      query.setParameter("startIndex", start); // map LocalDateTime → grainIndex
      query.setParameter("endIndex", end);
      return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
   }
}
