package com.dersaun.apicontas.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CalculoResumoFaturaUsuarioResponse implements Serializable {

    private Long pessoaId;
    private BigDecimal limiteUso;
    private BigDecimal porcentagem;
    private BigDecimal saldo;
    private BigDecimal totalMesAtual;
    private BigDecimal totalMesAnterior;

}
