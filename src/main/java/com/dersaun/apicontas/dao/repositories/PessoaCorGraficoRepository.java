package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.PessoaCorGrafico;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PessoaCorGraficoRepository extends JpaRepository<PessoaCorGrafico, Long> {

    @Query(
        "select p " +
        "from PessoaCorGrafico p "+
         "inner join fetch p.pessoa pessoa " +
         "order by p.id")
    FastList<PessoaCorGrafico> obterTudo();
}
