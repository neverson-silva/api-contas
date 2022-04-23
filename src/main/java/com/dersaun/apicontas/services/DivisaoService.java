package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dto.LancamentoDTO;

public interface DivisaoService {

    /**
     * Divide lancamentos
     * @param lancamentoDTO
     * @param lancamento
     * @return
     */
    Lancamento dividir(LancamentoDTO lancamentoDTO, Lancamento lancamento);

    /**
     * Divide lancamentos igualmente
     * @param lancamentoDTO
     * @param lancamento
     * @return
     */
    Lancamento dividirIgualmente(LancamentoDTO lancamentoDTO, Lancamento lancamento);

    /**
     * Dividir lancamentos com valores diferentes
     * @param lancamentoDTO
     * @param lancamento
     * @return
     */
    Lancamento dividirDiferente(LancamentoDTO lancamentoDTO, Lancamento lancamento);

    void checkValorTotal(Lancamento lancamento, LancamentoDTO lancamentoDTO);
}
