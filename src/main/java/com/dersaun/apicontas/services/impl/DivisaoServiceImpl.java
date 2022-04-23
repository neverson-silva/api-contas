package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dto.LancamentoDTO;
import com.dersaun.apicontas.services.DivisaoService;
import com.dersaun.apicontas.services.LancamentoService;
import com.dersaun.apicontas.util.NumberUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static com.dersaun.apicontas.exceptions.StandardHttpException.unprocessableEntity;

@Service
public class DivisaoServiceImpl implements DivisaoService {

    @Override
    public Lancamento dividir(LancamentoDTO lancamentoDTO, Lancamento lancamento) {
        if (lancamentoDTO.isIgual()) {
            return dividirIgualmente(lancamentoDTO, lancamento);
        } else {
            return dividirDiferente(lancamentoDTO, lancamento);
        }
    }

    @Override
    public Lancamento dividirIgualmente(LancamentoDTO lancamentoDTO, Lancamento lancamento) {
        var pessoas = lancamentoDTO.getPessoasDivididasIgualmente()
                                                    .select(idPessoa -> !lancamento.getPessoa().getId().equals(idPessoa));
        var valorPorPessoa = NumberUtil.roundValue(lancamentoDTO.getValor().doubleValue() / (pessoas.size() + 1) );

        pessoas.each(idPessoa -> {
            Lancamento novoLancamento = LancamentoService.criarRelacionado(lancamento, valorPorPessoa, idPessoa);
            novoLancamento.setDivisaoId(1);
            lancamento.getLancamentos().add(novoLancamento);
        });
        lancamento.setValorDividido(valorPorPessoa);
        lancamento.setDivisaoId(1);

        return lancamento;
    }

    @Override
    public Lancamento dividirDiferente(LancamentoDTO lancamentoDTO, Lancamento lancamento) {
        var pessoas = lancamentoDTO.getPessoasDivididasDiferente()
                .select(idPessoa -> !lancamento.getPessoa().getId().equals(idPessoa));
        AtomicReference<Double> valorTotal = new AtomicReference<Double>(lancamentoDTO.getValor().doubleValue());
        checkValorTotal(lancamento, lancamentoDTO);

        pessoas.each(pessoa -> {
            var novoLancamento = LancamentoService.criarRelacionado(lancamento, pessoa.getValor(), pessoa.getPessoaId());
            novoLancamento.setDivisaoId(2);
            valorTotal.updateAndGet(v -> v - pessoa.getValor().doubleValue());
            lancamento.getLancamentos().add(novoLancamento);
        });
        lancamento.setValorDividido(NumberUtil.roundValue(valorTotal.get()));
        lancamento.setDivisaoId(2);
        return lancamento;
    }

    @Override
    public void checkValorTotal(Lancamento lancamento, LancamentoDTO lancamentoDTO) {
        BigDecimal valorDividido = BigDecimal.ZERO;


        var valoresPessoas = lancamentoDTO.getPessoasDivididasDiferente()
                                                                .select(p -> !p.getPessoaId().equals(lancamento.getPessoa().getId()))
                                                                .collect(p -> p.getValor().doubleValue());
        valorDividido = NumberUtil.somar( valoresPessoas );

        if (valorDividido.doubleValue() > lancamento.getValor().doubleValue()) {
            throw unprocessableEntity("O valor dividido do lançamento ultrapassou o valor total.");
        }
    }

}
