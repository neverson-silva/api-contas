package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.contracts.UserCredentialDetails;
import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dao.models.FaturaItem;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.repositories.FaturaItemRepository;
import com.dersaun.apicontas.dto.MesAnoDTO;
import com.dersaun.apicontas.services.*;
import com.dersaun.apicontas.util.MesUtil;
import com.dersaun.apicontas.util.NumberUtil;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FaturaServiceImpl extends SimpleServiceImpl<FaturaItem, Long> implements FaturaService {

    @lombok.Getter
    @Autowired
    private FaturaItemRepository repository;

    @Autowired
    private MesService mesService;

    @Autowired
    private PaginationService<FaturaItem> pagination;

    /**
     * Recuperar todos os itens da fatura por mes e ano
     *
     * @param mesAnoDto
     * @return
     */
    @Override
    public MutableList<FaturaItem> getFaturaItemsByMesAno(MesAnoDTO mesAnoDto) {
        return getFaturaItemsByMesAno(mesAnoDto, false);
    }

    /**
     * Recuperar todos os items da fatura por mes e ano, e valida se é somente ativos
     *
     * @param mesAnoDto
     * @param somentAtivos
     * @return
     */
    @Override
    public MutableList<FaturaItem> getFaturaItemsByMesAno(MesAnoDTO mesAnoDto, Boolean somentAtivos) {
        if (somentAtivos) {
            return repository.findAllByFechamentoAndAnoAndNotPago(
                    mesAnoDto.getMesReferencia(),
                    mesAnoDto.getAnoReferencia()
            );
        }
        return repository.findAllByFechamentoAndAno(
                mesAnoDto.getMesReferencia(),
                mesAnoDto.getAnoReferencia()
        );
    }

    /**
     * Recupera itens da fatura por mes e ano com paginação
     *
     * @param mesAnoDto
     * @param somentAtivos
     * @param pageable
     * @return
     */
    @Override
    public Page<FaturaItem> getFaturaItemsByMesAno(MesAnoDTO mesAnoDto, Boolean somentAtivos, Pageable pageable) {
        if (somentAtivos) {
            return repository.findAllByFechamentoAndAnoAndNotPago(
                    mesAnoDto.getMesReferencia(),
                    mesAnoDto.getAnoReferencia(),
                    pageable
            );
        }
        return repository.findAllByFechamentoAndAno(
                mesAnoDto.getMesReferencia(),
                mesAnoDto.getAnoReferencia(),
                pageable
        );
    }

    /**
     * Recupera itens da fatura por mes e ano por pessoa com paginação
     *
     * @param mesAnoDto
     * @param pessoa
     * @param pageable
     * @return
     */
    @Override
    public Page<FaturaItem> getFaturaItemsByMesAnoAndPessoa(MesAnoDTO mesAnoDto, Pessoa pessoa, Pageable pageable) {
        return getFaturaItemsByMesAnoAndPessoa(mesAnoDto, pessoa, pageable, false);

    }

    /**
     * Recupera itens da fatura por mes e ano por pessoa com paginação
     *
     * @param mesAnoDto
     * @param pessoa
     * @param pageable
     * @param somenteAtivos
     * @return
     */
    @Override
    public Page<FaturaItem> getFaturaItemsByMesAnoAndPessoa(MesAnoDTO mesAnoDto, Pessoa pessoa, Pageable pageable, Boolean somenteAtivos) {
        if (somenteAtivos) {
            var valores = repository.findAllByFechamentoAndAnoAndPessoaAndNotPago(
                    mesAnoDto.getMesReferencia(),
                    mesAnoDto.getAnoReferencia(),
                    pessoa,
                    pageable
            );
            return valores;
        }
        return repository.findAllByFechamentoAndAnoAndPessoa(
                mesAnoDto.getMesReferencia(),
                mesAnoDto.getAnoReferencia(),
                pessoa,
                pageable
        );
    }

    /**
     * Recupera itens da fatura atual por mes e ano por pessoa com paginação
     *
     * @param page
     * @param linesPage
     * @param orderBy
     * @param direction
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FaturaItem> findLancamentosFaturaAtual(Integer page, Integer linesPage, String orderBy, String direction, Long pessoaId) {
        return findLancamentosFatura(mesService.getCurrent(), page, linesPage, orderBy, direction, true, pessoaId);
    }

    /**
     * Recupera itens da fatura atual por mes e ano por pessoa com paginação
     *
     * @param mesAnoDto
     * @param page
     * @param linesPerPage
     * @param orderBy
     * @param direction
     * @return
     */
    @Override
    public Page<FaturaItem> findLancamentosFatura(MesAnoDTO mesAnoDto, Integer page, Integer linesPerPage, String orderBy, String direction, Long pessoaId) {
        return findLancamentosFatura(mesAnoDto, page, linesPerPage, orderBy, direction, false, pessoaId);
    }

    /**
     * Recupera itens da fatura atual por mes e ano por pessoa com paginação
     *
     * @param mesAnoDto
     * @param page
     * @param linesPerPage
     * @param orderBy
     * @param direction
     * @param somenteAtivos
     * @return
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public Page<FaturaItem> findLancamentosFatura(MesAnoDTO mesAnoDto, Integer page, Integer linesPerPage, String orderBy, String direction, Boolean somenteAtivos, Long pessoaId) {
        UserCredentialDetails usuario = UsuarioService.usuario();

        Pageable pageRequest = PaginationService.createPageable(page, linesPerPage, orderBy, direction);

        if (usuario.isAdmin() && pessoaId == null) {
            return mapItensRelacionados(
                    getFaturaItemsByMesAno(mesAnoDto, somenteAtivos, pageRequest)
            );
        } else {
            var itens = getFaturaItemsByMesAnoAndPessoa(mesAnoDto, usuario.isAdmin() ? new Pessoa(pessoaId) : usuario.getPessoa(), pageRequest, somenteAtivos);

            return mapItensRelacionadosNotAdmin(itens);
        }
    }

    /**
     * Encontra valor dividido mes atual por pessoa
     *
     * @param pessoa
     * @return
     */
    @Override
    public BigDecimal findValorDividaMesAtualPorPessoa(Pessoa pessoa) {
        var mesAno = mesService.getCurrent();

        var lancamentos = repository.findAllByFechamentoAndAnoAndPessoaAtual(
                mesAno.getMesReferencia(), mesAno.getAnoReferencia(), pessoa
        );
        BigDecimal divida = lancamentos.stream().map(FaturaItem::getValorUtilizado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return NumberUtil.roundValue(divida);
    }

    /**
     * Encontra itens de fatura por palavra chave
     *
     * @param keyword
     * @param page
     * @param linesPerPage
     * @param orderBy
     * @param direction
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FaturaItem> findAllByKeyword(String keyword, Integer page, Integer linesPerPage,
                                             String orderBy, String direction, long mes, int ano) {
        UserCredentialDetails usuario = UsuarioService.usuario();
        MesAnoDTO mesAnoDto = mes > 0 ? MesUtil.getMesAnoReferencia(mes, ano) : mesService.getCurrent();
        keyword = "%" + keyword.toUpperCase() + "%";

        Pageable pageRequest = PaginationService.createPageable(page, linesPerPage, orderBy, direction);

        if (usuario.isAdmin()) {
            return mapItensRelacionados(findAllByKeywordAdmin(keyword.toUpperCase(), mesAnoDto, pageRequest));
        } else {
            return mapItensRelacionadosNotAdmin(findAllByKeywordUser(keyword.toUpperCase(), usuario.getPessoa(), mesAnoDto, pageRequest));
        }
    }

    /**
     * @param contaId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public FaturaItem findByContaIdAtual(Long contaId) {
        var mes = mesService.getCurrent();
        var faturaItem = mapItensRelacionados(repository.findByFechamentoAndAnoAndLancamentoId(mes.getMesReferencia(), mes.getAnoReferencia(), contaId));
        return faturaItem;
    }

    @Override
    @Transactional(readOnly = true)
    public FaturaItem findByContaIdMesAndAno(Long contaId, Long mes, Integer ano) {
        var faturaItem = repository.findByFechamentoAndAnoAndLancamentoId(mes, ano, contaId);
        return mapItensRelacionados(faturaItem);
    }

    public Page<FaturaItem>mapItensRelacionados(Page<FaturaItem> itens) {

        itens.forEach(faturaItem -> {
            if (faturaItem.isDividido() && faturaItem.hasLancamentosDivididos()) {
                var itensRelacionados = buildFaturaItemRelacionado(faturaItem, faturaItem.getLancamento().getLancamentos());
                faturaItem.addAllFaturaItemRelacionado(itensRelacionados);
            }
        });

        return itens;
    }

    public Page<FaturaItem> mapItensRelacionadosNotAdmin(Page<FaturaItem> itens) {

        for (FaturaItem faturaItem: itens) {
            if (faturaItem.isDividido()) {

                MutableList<Lancamento> lancamentos = new FastList<>();
                if (faturaItem.isPrincipal() && faturaItem.hasLancamentosDivididos()) {
                    lancamentos.addAll(faturaItem.getLancamento().getLancamentos());
                } else if (faturaItem.getLancamentoRelacionado().hasLancamentosDivididos()){
                    lancamentos.add(faturaItem.getLancamentoRelacionado());
                    lancamentos.addAll(faturaItem.getLancamentoRelacionado().getLancamentos());
                }

                var itensRelacionados = buildFaturaItemRelacionado(faturaItem, lancamentos);
                faturaItem.addAllFaturaItemRelacionado(itensRelacionados);
            }
        }

        return itens;
    }

    public List<FaturaItem> mapItensRelacionados(List<FaturaItem> itens) {
        itens.forEach(faturaItem -> {
            if (faturaItem.isDividido() && faturaItem.hasLancamentosDivididos()) {
                var itensRelacionados = buildFaturaItemRelacionado(faturaItem, faturaItem.getLancamento().getLancamentos());
                faturaItem.addAllFaturaItemRelacionado(itensRelacionados);
            }
        });

        return itens;
    }

    public FaturaItem mapItensRelacionados(FaturaItem faturaItem) {

        if (faturaItem.isDividido() && (faturaItem.hasLancamentosDivididos() ||
                (!UsuarioService.usuario().isAdmin() && faturaItem.getLancamentoRelacionado().hasLancamentosDivididos()))) {
            MutableList<Lancamento> lancamentos = new FastList<>();
            if (UsuarioService.usuario().isAdmin()) {
                lancamentos.addAll(faturaItem.getLancamento().getLancamentos());
            } else {
                if (faturaItem.isPrincipal()) {
                    lancamentos.addAll(faturaItem.getLancamento().getLancamentos());
                } else {
                    lancamentos.add(faturaItem.getLancamentoRelacionado());
                    lancamentos.addAll(faturaItem.getLancamentoRelacionado().getLancamentos());
                }
            }
            var itensRelacionados = buildFaturaItemRelacionado(faturaItem, lancamentos);
            faturaItem.addAllFaturaItemRelacionado(itensRelacionados);
        }
        return faturaItem;
    }

    private MutableList<FaturaItem> buildFaturaItemRelacionado(FaturaItem faturaItem, MutableList<Lancamento> lancamentoList) {

        return lancamentoList.reject(con -> con.getId().equals(faturaItem.getLancamento().getId()))
                .collect(conta -> {
                            try {
                                long id = (long) (Math.random() * ((faturaItem.getId() * 2) - faturaItem.getId() + 1) + faturaItem.getId());

                                FaturaItem fatItem = new FaturaItem();
                                fatItem.setId(id);
                                fatItem.setFechamento(faturaItem.getFechamento());
                                fatItem.setAno(faturaItem.getAno());
                                if (faturaItem.isParcelado()) {
                                    fatItem.setParcela(ParcelaService.createMockParcela(conta, faturaItem.getParcela()));
                                }
                                conta.setPessoa((Pessoa) Hibernate.unproxy(conta.getPessoa()));
                                fatItem.setLancamento(conta);
                                fatItem.setPessoaId(conta.getPessoa().getId());

                                return fatItem;
                            }catch (Exception e) {
                                System.out.println("Erro aqui");
                                System.out.println(e.getMessage());
                            }
                            return new FaturaItem();
                        });

    }

    private Page<FaturaItem> findAllByKeywordAdmin(String keyword, MesAnoDTO mesAnoDto, Pageable pageable) {
        var items = repository.search(
                mesAnoDto.getMesReferencia(), mesAnoDto.getAnoReferencia(), keyword, keyword, keyword, pageable);
        return items;
    }

    private Page<FaturaItem> findAllByKeywordUser(String keyword, Pessoa pessoa, MesAnoDTO mesAnoDto, Pageable pageable) {
        var items = repository.findAllByFechamentoAndAnoAndLancamentoPessoaAndLancamentoNomeOrFormaPagamentoNome(
                mesAnoDto.getMesReferencia(), mesAnoDto.getAnoReferencia(), pessoa, keyword, keyword, pageable);
        return items;
    }
}

