package com.dersaun.apicontas.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValorFormaPagamentoResponse implements Comparable<ValorFormaPagamentoResponse> {
    private Long id;
    private String type;
    private String nome;
    private BigDecimal valor;

    @Override
    public int compareTo(@NotNull ValorFormaPagamentoResponse o) {
        return nome.compareTo(o.getNome());
    }
}
