package com.dersaun.apicontas.services;

import org.eclipse.collections.api.list.MutableList;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Service<T, ID> {

    /**
     * Get the repository used by the service
     * @return
     */
    JpaRepository<T, ID> getRepository();

    long count();

    boolean existsById(ID id);

    /**
     * Find one entity by it's id
     * @param id
     * @return
     */
    Optional<T> findById(ID id);

    T getOne(ID id);

    /**
     * Find All records
     * @return
     */
    List<T> findAll();

    /**
     * Find all records ordened by
     * @param sort Sort
     * @return
     */
    MutableList<T> findAll(Sort sort);

    MutableList<T> findAllByIds(Iterable<ID> ids);

    /**
     * Update an entity
     * @param entidade
     * @return
     */
    T update(T entidade);

    MutableList<T> updateAll(Iterable<T> entidades);

    /**
     * Save a entity
     * @param entidade
     * @return
     */
    T save(T entidade);

    MutableList<T> saveAll(Iterable<T> entidades);

    /**
     * Delete an entity
     * @param entidade
     * @return
     */
    void delete(T entidade);

    void deleteAll(List<T> entidades);
}