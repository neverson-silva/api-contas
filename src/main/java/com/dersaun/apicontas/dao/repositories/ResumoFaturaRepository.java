package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.ResumoFatura;
import com.dersaun.apicontas.dao.models.ResumoFaturaId;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResumoFaturaRepository extends JpaRepository<ResumoFatura, ResumoFaturaId> {

    @Query("select rf " +
            "from ResumoFatura as rf " +
            "inner join fetch rf.pessoa " +
            "inner join fetch rf.formaPagamento formaPagamento " +
            "left join fetch formaPagamento.dono " +
            "inner join fetch rf.mes " +
            "where rf.id.devedorId = ?1 " +
                "and rf.id.mesId = ?2 " +
                "and rf.id.ano = ?3")
    FastList<ResumoFatura> findAllByPessoaIdAndMesAndAno(Long pessoaId, Long mesId, Integer ano);

    @Query("select rf " +
            "from ResumoFatura as rf " +
            "inner join fetch rf.pessoa " +
            "inner join fetch rf.formaPagamento formaPagamento " +
            "left join fetch formaPagamento.dono " +
            "inner join fetch rf.mes " +
            "where rf.id.mesId = ?1 " +
            "and rf.id.ano = ?2")
    FastList<ResumoFatura> findAllBydMesAndAno(Long mesId, Integer ano);
}
