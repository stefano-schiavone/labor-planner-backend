package com.laborplanner.backend.repository.custom;

import com.laborplanner.backend.repository.mapper.BaseMapper;
import java.util.List;
import java.util.Optional;

// This base repository interfave defines all default methods. It wants the Entity and Model because
// they will be mapped. The mapper to map them and the ID type string
public interface BaseRepositoryCustom<E, ID, M, MAPPER extends BaseMapper<M, E>> {

  // Create or update a model
  M save(M model);

  // Find a model by ID
  Optional<M> findById(ID id);

  // Get all models
  List<M> findAll();

  // Delete a model (by its entity ID)
  void deleteById(ID id);

  // Check if a record exists
  boolean existsById(ID id);
}
