package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.LimiteUso;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LimiteUsoRepository extends JpaRepository<LimiteUso, Long> {

    @NotNull
    @EntityGraph(attributePaths = "pessoa")
    Optional<LimiteUso> findById(@NotNull Long id);

    @NotNull
    @EntityGraph(attributePaths = "pessoa")
    FastList<LimiteUso> findAll();

    @NotNull
    @EntityGraph(attributePaths = "pessoa")
    FastList<LimiteUso> findAll(Sort sort);

    @NotNull
    @EntityGraph(attributePaths = "pessoa")
    LimiteUso findByPessoaId(Long pessoaId);
}
