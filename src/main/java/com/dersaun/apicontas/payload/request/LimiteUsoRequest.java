package com.dersaun.apicontas.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LimiteUsoRequest implements Serializable {

    private BigDecimal limite;
    private Long pessoaId;
}
