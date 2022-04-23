package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dao.models.Parcela;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelaResponse implements Serializable {

    private Long id;
    private Integer numero;
    private BigDecimal valor;
    private BigDecimal valorPago;
    private BigDecimal valorUtilizado;
    private Boolean pago;
    private Boolean atual;
    private Integer mesReferencia;
    private Integer anoReferencia;
    private String contaNome;

    public ParcelaResponse(Parcela parcela) {
        id = parcela.getId();
        numero = parcela.getNumero();
        valor = parcela.getValor();
        valorPago = parcela.getValorPago();
        pago = parcela.getPago();
        atual = parcela.getAtual();
        mesReferencia = parcela.getMesReferencia().getId().intValue();
        anoReferencia = parcela.getAnoReferencia();
        contaNome = parcela.getContaNome();
    }
}
