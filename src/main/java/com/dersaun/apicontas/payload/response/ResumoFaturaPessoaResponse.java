package com.dersaun.apicontas.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.list.MutableList;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoFaturaPessoaResponse implements Serializable {

    private Long id;
    private String nome;
    private String nomeCompleto;
    private String perfil;
    private BigDecimal total;
    private BigDecimal saldo;
    private MutableList<ValorFormaPagamentoResponse> formasPagamentos;
}
