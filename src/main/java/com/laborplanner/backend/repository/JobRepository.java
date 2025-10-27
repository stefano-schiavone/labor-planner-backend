package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.custom.JobRepositoryCustom;
import com.laborplanner.backend.repository.entity.JobEntity;
import com.laborplanner.backend.repository.mapper.JobMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepository extends BaseRepository<JobEntity, String, Job, JobMapper>
    implements JobRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final JobMapper mapper = JobMapper.INSTANCE;

  public JobRepository() {
    super(JobEntity.class, JobMapper.INSTANCE);
  }

  @Override
  public Optional<Job> findByName(String name) {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            JobEntity.class);
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
  public List<Job> findByTemplate(JobTemplate template) {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            JobEntity.class);
    query.setParameter("template", template);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findByRequiredMachineType(MachineType machineType) {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            JobEntity.class);
    query.setParameter("machineType", machineType);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findByDeadlineBefore(LocalDateTime deadline) {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            JobEntity.class);
    query.setParameter("deadline", deadline);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findByDeadlineAfter(LocalDateTime deadline) {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            JobEntity.class);
    query.setParameter("deadline", deadline);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findAllByOrderByDeadlineAsc() {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "", // <-- SQL/HQL query goes here
            JobEntity.class);
    List<JobEntity> entities = query.getResultList();
    return entities.stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
