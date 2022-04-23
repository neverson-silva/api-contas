package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.services.Service;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class SimpleServiceImpl<T, ID> implements Service<T, ID> {

    @Override
    public abstract JpaRepository<T, ID> getRepository();

    @Override
    public long count() {
        return getRepository().count();
    }

    @Override
    public boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    @Override
    public Optional<T> findById(ID id) {

        Optional<T> entidade = getRepository().findById(id);

        return entidade;
    }

    @Override
    public MutableList<T> findAll() {
        return new FastList<T>(getRepository().findAll());
    }

    @Override
    public MutableList<T> findAll(Sort sort) {
        return new FastList<T>(getRepository().findAll(sort));
    }

    @Override
    public T getOne(ID id) {
        return getRepository().getOne(id);
    }

    @Override
    public MutableList<T> findAllByIds(Iterable<ID> ids) {
        return new FastList<T>(getRepository().findAllById(ids));
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public T update(T entidade) {
        return getRepository().save(entidade);
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public MutableList<T> updateAll(Iterable<T> entidades) {
        return new FastList<T>(getRepository().saveAll(entidades));
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public T save(T entidade) {
        return getRepository().save(entidade);
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public MutableList<T> saveAll(Iterable<T> entidades) {
        return new FastList<T>(getRepository().saveAll(entidades));
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public void delete(T entidade) {
        getRepository().delete(entidade);
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public void deleteAll(List<T> entidades) {
        getRepository().deleteAll(entidades);
    }
}
