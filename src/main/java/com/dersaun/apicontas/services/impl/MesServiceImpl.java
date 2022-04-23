package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.FormaPagamento;
import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.dao.repositories.MesRepository;
import com.dersaun.apicontas.dto.MesAnoDTO;
import com.dersaun.apicontas.services.MesService;
import com.dersaun.apicontas.util.DateUtils;
import com.dersaun.apicontas.util.MesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;

import static com.dersaun.apicontas.services.impl.ResumoFaturaServiceImpl.CARNE;
import static com.dersaun.apicontas.services.impl.ResumoFaturaServiceImpl.DINHEIRO;

@Service
public class MesServiceImpl implements MesService {

    @Autowired
    private MesRepository repository;

    @Override
    public Mes getMesAtual() {
        return repository.findByAtualIsTrue();
    }

    @Override
    public MesAnoDTO getCurrent() {
        return MesUtil.getMesAnoReferencia(getMesAtual());
    }

    @Override
    public Mes findById(Long mes) {
        return repository.findById(mes).get();
    }

    @Override
    public Mes getFromDataCompraAndFormaPagamento(LocalDate dataCompra, FormaPagamento formaPagamento) {
        var mesAtual = getMesAtual();
        var hoje = LocalDate.now();
        var mesCompra = dataCompra.getMonthValue();

        var ultimoDiaMesDataCompra = DateUtils.getLastDayOfMonth(dataCompra);
        if (Arrays.asList(DINHEIRO, CARNE).contains(formaPagamento.getId())) {
            return new Mes((long) mesCompra);
        }
        if (formaPagamento.getNome().toLowerCase().contains("nubank") && mesAtual.getId().intValue() + 1 == mesCompra && hoje.getDayOfMonth() <= 3) {
            return mesAtual;
        } else if (dataCompra.getDayOfMonth() <= 3 && formaPagamento.getNome().toLowerCase().contains("nubank")) {
            var newMes = dataCompra.minusMonths(1);
            return new Mes((long) newMes.getMonthValue());
        } else if (ultimoDiaMesDataCompra == dataCompra.getDayOfMonth() && !formaPagamento.getNome().toLowerCase().contains("nubank")) {
            var newMes = dataCompra.plusMonths(1);
            return new Mes((long) newMes.getMonthValue());
        } else if (mesAtual.getId().intValue() == mesCompra) {
            return mesAtual;
        }
        return new Mes((long) mesCompra);
    }
}
