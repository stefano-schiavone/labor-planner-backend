package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.repository.mapper.BaseMapper;
import java.util.List;
import java.util.Optional;

// This base repository interfave defines all default methods. It wants the Entity and Model because
// they will be mapped. The mapper to map them and the UUID type string
public interface BaseRepositoryCustom<E, UUID, M, MAPPER extends BaseMapper<M, E>> {

  // Create or update a model
  M save(M model);

  // Find a model by UUID
  Optional<M> findByUuid(UUID uuid);

  // Get all models
  List<M> findAll();

  // Delete a model (by its entity UUID)
  void deleteByUuid(UUID uuid);

  // Check if a record exists
  boolean existsByUuid(UUID uuid);
}
