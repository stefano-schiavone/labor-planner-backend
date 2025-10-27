package com.laborplanner.backend.repository.mapper;

public interface BaseMapper<M, E> {
  M toModel(E entity);

  E toEntity(M model);
}
