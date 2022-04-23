package com.dersaun.apicontas.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AtualizarFormaPagamentoRequestDto implements Serializable {

    private String nome;
    private String cor;
    private Long pessoaId;
    private Integer diaVencimento;
}
