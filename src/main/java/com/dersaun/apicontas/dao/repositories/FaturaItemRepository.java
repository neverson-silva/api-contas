package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.FaturaItem;
import com.dersaun.apicontas.dao.models.Pessoa;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FaturaItemRepository extends JpaRepository<FaturaItem, Long> {

    @Query("select fa " +
            "from FaturaItem fa " +
            "left join fetch fa.parcela pa " +
            "inner join fetch pa.mesReferencia " +
            "inner join fetch fa.lancamento lancamento " +
            "inner join fetch lancamento.pessoa " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "inner join fetch lancamento.mes " +
            "left join fetch lancamento.lancamentoRelacionado " +
            "where fa.fechamento = :fechamento " +
            "and fa.ano = :ano " +
            "and (lancamento.pago <> true or lancamento.parcelado = true and pa.pago <> true)" +
            "order by lancamento.dataCompra desc")
    FastList<FaturaItem> findAllByFechamentoAndAnoAndNotPago(@Param("fechamento") Long fechamento,
                                                             @Param("ano") Integer ano);


    @Query("select fa " +
            "from FaturaItem fa " +
            "left join fetch fa.parcela pa " +
            "inner join fetch pa.mesReferencia " +
            "inner join fetch fa.lancamento lancamento " +
            "inner join fetch lancamento.pessoa " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "inner join fetch lancamento.mes " +
            "left join fetch lancamento.lancamentoRelacionado " +
            "where fa.fechamento = :fechamento " +
            "and fa.ano = :ano " +
            "order by lancamento.dataCompra desc")
    FastList<FaturaItem> findAllByFechamentoAndAno(@Param("fechamento") Long fechamento,
                                               @Param("ano") Integer ano);

    @Query(value = "select fa " +
            "from FaturaItem fa " +
            "left join fetch fa.parcela pa " +
            "left join fetch pa.mesReferencia " +
            "inner join fetch fa.lancamento lancamento " +
            "inner join fetch lancamento.pessoa " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "left join fetch formaPagamento.dono " +
            "inner join fetch lancamento.mes " +
            "left join fetch lancamento.lancamentoRelacionado " +
            "left join fetch lancamento.lancamentos " +
            "where fa.fechamento = :fechamento " +
            "and fa.ano = :ano " +
            "and lancamento.lancamentoRelacionado is null " +
            "and ((lancamento.pago <> true) or (lancamento.parcelado = true and pa.pago <> true)) " +
            "",
            countQuery = "select count(fi) " +
                    "from FaturaItem fi " +
                    "inner join fi.lancamento lancamento " +
                    "left join fi.parcela pa " +
                    "where fi.fechamento = :fechamento " +
                    "and fi.ano = :ano " +
                    "and lancamento.lancamentoRelacionado is null " +
                    "and ((lancamento.pago <> true) or (lancamento.parcelado = true and pa.pago <> true)) "
    )
    Page<FaturaItem> findAllByFechamentoAndAnoAndNotPago(@Param("fechamento") Long fechamento,
                                                         @Param("ano") Integer ano, Pageable pageable);

    @EntityGraph(attributePaths = {"parcela", "parcela.mesReferencia", "lancamento", "lancamento.pessoa", "formaPagamento", "formaPagamento.dono", "lancamento.lancamentoRelacionado", "lancamento.lancamentos"})
    @Query(value = "select fa " +
            "from FaturaItem fa " +
//            "left join fetch fa.parcela pa " +
//            "inner join fetch pa.mesReferencia " +
//            "inner join fetch fa.lancamento lancamento " +
//            "inner join fetch lancamento.pessoa " +
//            "inner join fetch lancamento.formaPagamento formaPagamento " +
//            "left join fetch formaPagamento.dono " +
//            "inner join fetch lancamento.mes " +
//            "left join fetch lancamento.lancamentoRelacionado " +
//            "left join fetch lancamento.lancamentos " +
            "where fa.fechamento = :fechamento " +
            "and fa.lancamento.lancamentoRelacionado is null " +
            "and fa.ano = :ano " +
            "order by fa.lancamento.dataCompra desc",
            countQuery = "select count(fi) " +
                    "from FaturaItem fi " +
                    "inner join fi.lancamento lancamento " +
                    "left join fi.parcela pa " +
                    "where fi.fechamento = :fechamento " +
                    "and fi.ano = :ano " +
                    "and lancamento.lancamentoRelacionado is null"
    )
    Page<FaturaItem> findAllByFechamentoAndAno(@Param("fechamento") Long fechamento,
                                               @Param("ano") Integer ano, Pageable pageable);

    @Query(value = "select fa " +
            "from FaturaItem fa " +
            "left join fetch fa.parcela pa " +
            "left join fetch pa.mesReferencia " +
            "inner join fetch fa.lancamento lancamento " +
            "inner join fetch lancamento.pessoa pessoa " +
            "inner join fetch lancamento.mes " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "left join fetch formaPagamento.dono " +
            "left join fetch lancamento.lancamentoRelacionado " +
            "left join fetch lancamento.lancamentos " +
            "where fa.fechamento = :fechamento " +
            "and fa.ano = :ano " +
            "and pessoa = :pessoa " +
            "and (lancamento.pago <> true or lancamento.parcelado = true and pa.pago <> true) ",
            countQuery = "select count(fi) " +
                    "from FaturaItem fi " +
                    "where fi.fechamento = :fechamento " +
                    "and fi.ano = :ano " +
                    "and fi.lancamento.pessoa = :pessoa " +
                    "and (fi.lancamento.pago <> true or fi.lancamento.parcelado = true and fi.parcela.pago <> true) ")
    Page<FaturaItem> findAllByFechamentoAndAnoAndPessoaAndNotPago(@Param("fechamento") Long fechamento,
                                                                  @Param("ano") Integer ano,
                                                                  @Param("pessoa") Pessoa pessoa, Pageable pageRequest);

    @Query(value = "select fa " +
            "from FaturaItem fa " +
            "left join fetch fa.parcela pa " +
            "left join fetch pa.mesReferencia " +
            "inner join fetch fa.lancamento lancamento " +
            "inner join fetch lancamento.pessoa pessoa " +
            "inner join fetch lancamento.mes " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "left join fetch formaPagamento.dono " +
            "left join fetch lancamento.lancamentoRelacionado lancamentoRelacionado " +
            "left join fetch lancamento.lancamentos " +
            "where fa.fechamento = :fechamento " +
            "and fa.ano = :ano " +
            "and pessoa = :pessoa ",
            countQuery = "select count(fi) " +
                    "from FaturaItem fi " +
                    "where fi.fechamento = :fechamento " +
                    "and fi.ano = :ano " +
                    "and fi.lancamento.pessoa = :pessoa")
    Page<FaturaItem> findAllByFechamentoAndAnoAndPessoa(@Param("fechamento") Long fechamento,
                                                        @Param("ano") Integer ano,
                                                        @Param("pessoa") Pessoa pessoa, Pageable pageRequest);

    @Query("select fa " +
            "from FaturaItem fa " +
            "left join fetch fa.parcela pa " +
            "inner join fetch fa.lancamento lancamento " +
            "inner join fetch lancamento.pessoa pessoa " +
            "inner join fetch lancamento.mes " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "left join fetch lancamento.lancamentoRelacionado " +
//            "left join fetch lancamento.lancamentos " +
            "where fa.fechamento = :fechamento " +
            "and fa.ano = :ano " +
            "and pessoa = :pessoa " +
            "and (lancamento.pago <> true or lancamento.parcelado = true and pa.pago <> true)")
    FastList<FaturaItem> findAllByFechamentoAndAnoAndPessoaAtual(@Param("fechamento") Long fechamento,
                                                             @Param("ano") Integer ano,
                                                             @Param("pessoa") Pessoa pessoa);

    @Query(value = " select fi " +
            "from FaturaItem  fi " +
            "inner join fetch fi.lancamento lancamento " +
            "left join fetch fi.parcela parcela " +
            "left join fetch parcela.mesReferencia " +
            "left join fetch lancamento.lancamentoRelacionado " +
            "left join fetch lancamento.lancamentos " +
            "inner join fetch lancamento.pessoa pessoa " +
            "inner join fetch lancamento.mes " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "where fi.fechamento = :fechamento " +
            "and fi.ano = :ano " +
            " and lancamento.lancamentoRelacionado is null " +

            "and ((UPPER(lancamento.nome) LIKE :lancamentoNome or UPPER(formaPagamento.nome) LIKE :cartaoNome or UPPER(fi.lancamento.pessoa.nome) like :pessoaNome)  or " +
            "(UPPER(lancamento.nome) LIKE :lancamentoNome or UPPER(formaPagamento.nome) LIKE :cartaoNome or UPPER(fi.lancamento.pessoa.nome) like :pessoaNome))",
            countQuery = "select count(fi) " +
                    "from FaturaItem fi " +
                    "inner join fi.lancamento lancamento " +
                    "inner join lancamento.formaPagamento formaPagamento " +
                    "where fi.fechamento = :fechamento " +
                    " and fi.ano  = :ano " +
                    "and lancamento.lancamentoRelacionado is null " +
                    "and ((UPPER(lancamento.nome) LIKE :lancamentoNome or UPPER(formaPagamento.nome) LIKE :cartaoNome or UPPER(fi.lancamento.pessoa.nome) like :pessoaNome)  or " +
                    "(UPPER(lancamento.nome) LIKE :lancamentoNome or UPPER(formaPagamento.nome) LIKE :cartaoNome or UPPER(lancamento.pessoa.nome) like :pessoaNome) )"
    )
    Page<FaturaItem> search(
            @Param("fechamento") Long fechamento,
            @Param("ano") Integer ano,
            @Param("pessoaNome") String pessoaNome,
            @Param("lancamentoNome") String lancamentoNome,
            @Param("cartaoNome") String cartaoNome,
            Pageable pageable
    );

    @Query(value = " select distinct fi " +
            "from FaturaItem  fi " +
            "inner join fetch fi.lancamento lancamento " +
            "left join fetch fi.parcela parcela " +
            "left join fetch parcela.mesReferencia " +
            "inner join fetch lancamento.pessoa pessoa " +
            "inner join fetch lancamento.mes " +
            "inner join fetch lancamento.formaPagamento formaPagamento " +
            "where fi.fechamento = :fechamento " +
            "and fi.ano = :ano " +
            "and lancamento.pessoa = :pessoa " +
            "and lancamento.nome LIKE %:lancamentoNome% " +
            "or formaPagamento.nome LIKE %:cartaoNome%",
            countQuery = " select count(fi) " +
                    "from FaturaItem  fi " +
                    "where fi.fechamento = :fechamento " +
                    "and fi.ano = :ano " +
                    "and fi.lancamento.pessoa = :pessoa " +
                    "and UPPER(fi.lancamento.nome) LIKE %:lancamentoNome% " +
                    "or UPPER(fi.lancamento.formaPagamento.nome) LIKE %:cartaoNome% "
    )
    Page<FaturaItem> findAllByFechamentoAndAnoAndLancamentoPessoaAndLancamentoNomeOrFormaPagamentoNome(
            @Param("fechamento") Long fechamento,
            @Param("ano") Integer ano,
            @Param("pessoa") Pessoa pessoa,
            @Param("lancamentoNome") String lancamentoNome,
            @Param("cartaoNome") String cartaoNome, Pageable page
    );

    @EntityGraph(attributePaths = {"parcela", "lancamento", "lancamento.mes", "lancamento.lancamentoRelacionado", "lancamento.lancamentos",
            "lancamento.pessoa", "lancamento.formaPagamento", "parcela.mesReferencia", "lancamento.formaPagamento.dono"})
    FaturaItem findByFechamentoAndAnoAndLancamentoId(Long fechamento, Integer ano, Long lancamentoId);
}