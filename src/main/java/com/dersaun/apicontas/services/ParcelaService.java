package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.dao.models.Parcela;
import com.dersaun.apicontas.dto.LancamentoDTO;
import com.dersaun.apicontas.dto.Parcelamento;
import org.eclipse.collections.api.list.MutableList;

import java.util.Optional;

public interface ParcelaService extends Service<Parcela, Long>{

    /**
     * Parcela varias contas
     * @param parcelamentos
     */
    void parcelar(MutableList<Parcelamento> parcelamentos);

    /**
     * Parcela uma conta
     * @param parcelamento
     * @return
     */
    MutableList<Parcela> parcelar(Parcelamento parcelamento);

    void atualizar(Lancamento lancamento, Boolean eraParcelado, Boolean atualmenteParcelado, int quantidadeParcelasOriginal, boolean reparcelar, Mes mesAtual);

    void atualizar(Lancamento lancamento, Boolean eraParcelado, Boolean parcelado, int quantidadeParcelasOriginal, Mes mesAtual);

    static Parcela createMockParcela(Lancamento lancamento, Parcela parcela) {
        var parcel = new Parcela();

        parcel.setId(lancamento.getId() * parcela.getId());
        parcel.setVencimento(
                Optional.ofNullable(lancamento.getFormaPagamento().getVencimento())
                        .orElse(parcela.getVencimento())
        );
        parcel.setNumero(parcela.getNumero());
        parcel.setValor(lancamento.getValorPorParcela());

        if (parcela.isPago()) {
            parcel.setValorPago(parcel.getValor());
        }
        parcel.setLancamento(lancamento);
        parcel.setPago(parcela.isPago());
        parcel.setAtual(parcela.isAtual());
        parcel.setMesReferencia(parcela.getMesReferencia());
        parcel.setAnoReferencia(parcela.getAnoReferencia());
        parcel.setCreatedAt(parcela.getCreatedAt());
        parcel.setUpdatedAt(parcela.getUpdatedAt());

        return parcel;
    }

//    Page<Parcela> findAllByContaId(Integer contaId, Pageable pageRequest);
//
//    void updateOne(Integer id, ParcelaDTO parcelaDto);
//
//    void updateMany(List<ParcelaDTO> parcelas);

    boolean setParcelaAtual(Parcela parcela);


}
