package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.FaturaItem;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dto.MesAnoDTO;
import org.eclipse.collections.api.list.MutableList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Fatura
 */
public interface FaturaService extends Service<FaturaItem, Long> {

    /**
     * Recuperar todos os itens da fatura por mes e ano
     * @param mesAnoDto
     * @return
     */
    MutableList<FaturaItem> getFaturaItemsByMesAno(MesAnoDTO mesAnoDto);

    /**
     * Recuperar todos os items da fatura por mes e ano, e valida se é somente ativos
     * @param mesAnoDto
     * @param somentAtivos
     * @return
     */
    MutableList<FaturaItem> getFaturaItemsByMesAno(MesAnoDTO mesAnoDto, Boolean somentAtivos);

    /**
     * Recupera itens da fatura por mes e ano com paginação
     * @param mesAnoDto
     * @param somentAtivos
     * @param pageable
     * @return
     */
    Page<FaturaItem> getFaturaItemsByMesAno(MesAnoDTO mesAnoDto, Boolean somentAtivos, Pageable pageable);

    /**
     *  Recupera itens da fatura por mes e ano por pessoa com paginação
     * @param mesAnoDto
     * @param pessoa
     * @param pageable
     * @return
     */
    Page<FaturaItem> getFaturaItemsByMesAnoAndPessoa(MesAnoDTO mesAnoDto, Pessoa pessoa, Pageable pageable);

    /**
     * Recupera itens da fatura por mes e ano por pessoa com paginação
     * @param mesAnoDto
     * @param pessoa
     * @param pageable
     * @param somenteAtivos
     * @return
     */
    Page<FaturaItem> getFaturaItemsByMesAnoAndPessoa(MesAnoDTO mesAnoDto, Pessoa pessoa,
                                                     Pageable pageable, Boolean somenteAtivos);

    /**
     * Recupera itens da fatura atual por mes e ano por pessoa com paginação
     * @param page
     * @param linesPage
     * @param orderBy
     * @param direction
     * @return
     */
    Page<FaturaItem> findLancamentosFaturaAtual(Integer page, Integer linesPage, String orderBy, String direction, Long pessoaId);

    /**
     * Recupera itens da fatura atual por mes e ano por pessoa com paginação
     * @param mesAnoDto
     * @param page
     * @param linesPerPage
     * @param orderBy
     * @param direction
     * @return
     */
    Page<FaturaItem> findLancamentosFatura(MesAnoDTO mesAnoDto,
                                           Integer page,
                                           Integer linesPerPage,
                                           String orderBy,
                                           String direction, Long pessoaId);

    /**
     * Recupera itens da fatura atual por mes e ano por pessoa com paginação
     * @param mesAnoDto
     * @param page
     * @param linesPerPage
     * @param orderBy
     * @param direction
     * @param somenteAtivos
     * @return
     */
    Page<FaturaItem> findLancamentosFatura(MesAnoDTO mesAnoDto,
                                           Integer page,
                                           Integer linesPerPage,
                                           String orderBy,
                                           String direction,
                                           Boolean somenteAtivos,
                                           Long pessoaId);

    /**
     * Encontra valor dividido mes atual por pessoa
     * @param pessoa
     * @return
     */
    BigDecimal findValorDividaMesAtualPorPessoa(Pessoa pessoa);

    /**
     * Encontra itens de fatura por palavra chave
     * @param keyword
     * @param page
     * @param linesPerPage
     * @param orderBy
     * @param direction
     * @return
     */
    Page<FaturaItem> findAllByKeyword(String keyword, Integer page, Integer linesPerPage,
                                      String orderBy, String direction, long mes, int ano);

    /**
     *
     * @param contaId
     * @return
     */
    FaturaItem findByContaIdAtual(Long contaId);

    FaturaItem findByContaIdMesAndAno(Long contaId, Long mes, Integer ano);
}
