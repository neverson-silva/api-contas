package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.Lancamento;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @NotNull
    @EntityGraph(attributePaths = { "pessoa", "formaPagamento", "mes"})
    Optional<Lancamento> findById(Long id);

    @Modifying()
    @Query("delete from Lancamento where lancamentoRelacionado = ?1")
    void deleteByLancamentoRelacionado(Lancamento lancamento);
}
