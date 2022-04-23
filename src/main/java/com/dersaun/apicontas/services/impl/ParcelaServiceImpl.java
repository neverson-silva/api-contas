package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.dao.models.Parcela;
import com.dersaun.apicontas.dao.repositories.ParcelaRepository;
import com.dersaun.apicontas.dto.Parcelamento;
import com.dersaun.apicontas.services.ParcelaService;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class ParcelaServiceImpl extends SimpleServiceImpl<Parcela, Long> implements ParcelaService {

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Override
    public void parcelar(MutableList<Parcelamento> parcelamentos) {
        parcelamentos.each(this::parcelar);
    }

    @Override
    public MutableList<Parcela> parcelar(Parcelamento parcelamento) {
        var parcelas = new FastList<Parcela>();
        var quantidadeParcelas = parcelamento.getParcelasCriar();
        var start = parcelamento.getStart();

        IntStream.rangeClosed(start, quantidadeParcelas)
                .forEach(numero -> parcelas.add(create(numero, parcelamento, true)));

        var salvas = saveAll(parcelas);

        return salvas;
    }

    @Override
    public void atualizar(Lancamento lancamento, Boolean eraParcelado, Boolean atualmenteParcelado, int quantidadeParcelasOriginal, boolean reparcelar, Mes mesAtual) {

    }

    @Override
    public void atualizar(Lancamento lancamento, Boolean eraParcelado, Boolean parcelado, int quantidadeParcelasOriginal, Mes mesAtual) {

    }

    @Override
    public boolean setParcelaAtual(Parcela parcela) {
        return false;
    }

    @Override
    public JpaRepository<Parcela, Long> getRepository() {
        return parcelaRepository;
    }

    /**
     * Cria uma parcela a partir dos parametros
     *
     * @param numero
     * @param parcelamento
     * @param shouldIncrement
     * @return
     */
    private Parcela create(int numero, Parcelamento parcelamento, boolean shouldIncrement) {
        var atual = parcelamento.checkIsAtual(numero);
        var pago = parcelamento.checkIsPago(numero);
        int vencimento = Optional.ofNullable(parcelamento.getLancamento().getFormaPagamento().getVencimento())
                .orElse(9);
        var parcela = new Parcela(
                vencimento,
                numero,
                parcelamento.getValorPorParcela(),
                pago ? parcelamento.getValorPorParcela() : BigDecimal.ZERO,
                parcelamento.getLancamento(),
                pago,
                atual);
        parcela.setMesReferencia(new Mes(parcelamento.getMesAno().getMesReferencia()));
        parcela.setAnoReferencia(parcelamento.getMesAno().getAnoReferencia());

        if (shouldIncrement) {
            parcelamento.setMesAno(
                    parcelamento.getMesAno().getMesReferencia(),
                    parcelamento.getMesAno().getAnoReferencia()
            );
        }
        return parcela;
    }
}
