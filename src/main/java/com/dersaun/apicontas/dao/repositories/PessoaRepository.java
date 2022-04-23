package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.Pessoa;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    @Query(" select p " +
            "from Pessoa p " +
            "where p.id not in (select l.pessoa.id from LimiteUso l) " +
            "order by p.nome asc")
    FastList<Pessoa> findAllSemLimiteUso();
}
