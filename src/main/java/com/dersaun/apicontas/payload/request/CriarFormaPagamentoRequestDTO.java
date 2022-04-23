package com.dersaun.apicontas.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriarFormaPagamentoRequestDTO implements Serializable {

    private Long pessoaId;

    private String nome;

    private Integer diaVencimento;

    private String cor;
}
