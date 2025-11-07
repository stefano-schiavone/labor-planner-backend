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
public class JobRepository extends BaseRepository<JobEntity, Job, JobMapper>
    implements JobRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final JobMapper mapper = JobMapper.INSTANCE;

  public JobRepository() {
    super(JobEntity.class, JobMapper.INSTANCE);
  }

  @Override
  public Optional<Job> findByName(String name) {
    TypedQuery<JobEntity> query =
        em.createQuery("SELECT j FROM JobEntity j WHERE j.name = :name", JobEntity.class);
    query.setParameter("name", name);
    return query.getResultStream().findFirst().map(mapper::toModel);
  }

  @Override
  public boolean existsByName(String name) {
    TypedQuery<Long> query =
        em.createQuery("SELECT COUNT(j) FROM JobEntity j WHERE j.name = :name", Long.class);
    query.setParameter("name", name);
    Long count = query.getSingleResult();
    return count != null && count > 0;
  }

  @Override
  public List<Job> findByTemplate(JobTemplate template) {
    // Assuming JobTemplateEntity is linked through job_template_uuid
    TypedQuery<JobEntity> query =
        em.createQuery(
            "SELECT j FROM JobEntity j WHERE j.template.templateUuid = :templateUuid",
            JobEntity.class);
    query.setParameter("templateUuid", template.getJobTemplateUuid());
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findByRequiredMachineType(MachineType machineType) {
    // Assuming MachineTypeEntity is linked through required_machine_type_uuid
    TypedQuery<JobEntity> query =
        em.createQuery(
            "SELECT j FROM JobEntity j WHERE j.requiredMachineType.machineTypeUuid ="
                + " :machineTypeUuid",
            JobEntity.class);
    query.setParameter("machineTypeUuid", machineType.getMachineTypeUuid());
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findByDeadlineBefore(LocalDateTime deadline) {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "SELECT j FROM JobEntity j WHERE j.deadline < :deadline ORDER BY j.deadline ASC",
            JobEntity.class);
    query.setParameter("deadline", deadline);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findByDeadlineAfter(LocalDateTime deadline) {
    TypedQuery<JobEntity> query =
        em.createQuery(
            "SELECT j FROM JobEntity j WHERE j.deadline > :deadline ORDER BY j.deadline ASC",
            JobEntity.class);
    query.setParameter("deadline", deadline);
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<Job> findAllByOrderByDeadlineAsc() {
    TypedQuery<JobEntity> query =
        em.createQuery("SELECT j FROM JobEntity j ORDER BY j.deadline ASC", JobEntity.class);
    List<JobEntity> entities = query.getResultList();
    return entities.stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
