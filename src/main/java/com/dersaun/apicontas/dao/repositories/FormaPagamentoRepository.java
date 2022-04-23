package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.FormaPagamento;
import com.dersaun.apicontas.dao.models.Pessoa;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {

    FastList<FormaPagamento> findAllByAtivoTrueOrderByNomeAsc();

    FastList<FormaPagamento> findAllByAtivoTrueAndDonoOrIdInOrderByNomeAsc(Pessoa dono, List<Long> in);

    FastList<FormaPagamento> findAllByOrderByNomeAsc();

    FastList<FormaPagamento> findAllByDonoOrIdInOrderByNomeAsc(Pessoa dono, List<Long> in);

    @EntityGraph(attributePaths = "dono")
    Optional<FormaPagamento> findById(Long id);
}
