package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dto.LancamentoDTO;

import java.math.BigDecimal;

public interface LancamentoService extends Service<Lancamento, Long> {

    Lancamento getOne(Long id);

    void delete(Long id);

    Lancamento criarLancamento(LancamentoDTO lancamentoDTO);

    void atualizar(Long id, LancamentoDTO lancamentoDTO);

    /**
     * Atualizar uma conta dividida com valores diferentes
     * @param conta
     * @param contaDto
     * @param parcelado
     * @param mesAtual
     * @return
     */
    Boolean atualizarDiferente(Lancamento lancamento, LancamentoDTO lancamentoDto, Boolean parcelado, Mes mesAtual);

    /**
     * Atualizar uma lancamento normal, sem divisões
     * @param lancamento
     * @param lancamentoDto
     * @param parcelado
     * @param mesAtual
     * @return
     */
    Boolean atualizarContaNormal(Lancamento lancamento, LancamentoDTO lancamentoDto, Boolean parcelado, Mes mesAtual);

    /**
     * Atualizar uma lancamento dividida de forma igualitaria
     * @param lancamento
     * @param lancamentoDto
     * @param parcelado
     * @param mesAtual
     * @return
     */
    Boolean atualizarIgualmente(Lancamento lancamento, LancamentoDTO lancamentoDto, Boolean parcelado, Mes mesAtual);

    /**
     * Apagar as lancamentos divididas
     * @param lancamento
     * @param removerTudo
     */
    void removerContasDivididas(Lancamento lancamento, Boolean removerTudo);

    static Lancamento criarRelacionado(Lancamento lancamento, BigDecimal valorPorPessoa, Long pessoaId) {
        var novoLancamento = new Lancamento();
        novoLancamento.setNome(lancamento.getNome());
        novoLancamento.setDescricao(lancamento.getDescricao());
        novoLancamento.setValor(lancamento.getValor());
        novoLancamento.setDataCompra(lancamento.getDataCompra());
        novoLancamento.setAno(lancamento.getAno());
        novoLancamento.setQuantidadeParcelas(lancamento.getQuantidadeParcelas());
        novoLancamento.setPessoa(new Pessoa(pessoaId));
        novoLancamento.setParcelado(lancamento.getParcelado());
        novoLancamento.setFormaPagamento(lancamento.getFormaPagamento());
        novoLancamento.setMes(lancamento.getMes());
        novoLancamento.setPago(lancamento.getPago());
        novoLancamento.setTipoConta(lancamento.getTipoConta());
        novoLancamento.setValorDividido(valorPorPessoa);
        novoLancamento.setLancamentoRelacionado(lancamento);

        return novoLancamento;
    }
}
