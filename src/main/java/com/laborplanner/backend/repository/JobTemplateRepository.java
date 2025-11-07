package com.laborplanner.backend.repository;

import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.custom.JobTemplateRepositoryCustom;
import com.laborplanner.backend.repository.entity.JobTemplateEntity;
import com.laborplanner.backend.repository.mapper.JobTemplateMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JobTemplateRepository
    extends BaseRepository<JobTemplateEntity, JobTemplate, JobTemplateMapper>
    implements JobTemplateRepositoryCustom {

  @PersistenceContext private EntityManager em;

  private final JobTemplateMapper mapper = JobTemplateMapper.INSTANCE;

  public JobTemplateRepository() {
    super(JobTemplateEntity.class, JobTemplateMapper.INSTANCE);
  }

  @Override
  public Optional<JobTemplate> findByName(String name) {
    TypedQuery<JobTemplateEntity> query =
        em.createQuery(
            "SELECT jt FROM JobTemplateEntity jt WHERE jt.name = :name", JobTemplateEntity.class);
    query.setParameter("name", name);
    return query.getResultStream().findFirst().map(mapper::toModel);
  }

  @Override
  public boolean existsByName(String name) {
    TypedQuery<Long> query =
        em.createQuery(
            "SELECT COUNT(jt) FROM JobTemplateEntity jt WHERE jt.name = :name", Long.class);
    query.setParameter("name", name);
    Long count = query.getSingleResult();
    return count != null && count > 0;
  }

  @Override
  public List<JobTemplate> findByRequiredMachineType(MachineType machineType) {
    TypedQuery<JobTemplateEntity> query =
        em.createQuery(
            "SELECT jt FROM JobTemplateEntity jt WHERE jt.requiredMachineType.machineTypeUuid ="
                + " :machineTypeUuid",
            JobTemplateEntity.class);
    query.setParameter("machineTypeUuid", machineType.getMachineTypeUuid());
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<JobTemplate> findByCreatedByUser(User user) {
    TypedQuery<JobTemplateEntity> query =
        em.createQuery(
            "SELECT jt FROM JobTemplateEntity jt WHERE jt.createdByUser.userUuid = :userUuid",
            JobTemplateEntity.class);
    query.setParameter("userUuid", user.getUserUuid());
    return query.getResultList().stream().map(mapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<JobTemplate> findAllByOrderByNameAsc() {
    TypedQuery<JobTemplateEntity> query =
        em.createQuery(
            "SELECT jt FROM JobTemplateEntity jt ORDER BY jt.name ASC", JobTemplateEntity.class);
    List<JobTemplateEntity> entities = query.getResultList();
    return entities.stream().map(mapper::toModel).collect(Collectors.toList());
  }
}
