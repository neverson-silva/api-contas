package com.dersaun.apicontas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PessoasDivisaoDiferenteDTO implements Serializable {
    private Long pessoaId;
    private BigDecimal valor;
}
