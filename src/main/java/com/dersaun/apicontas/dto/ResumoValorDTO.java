package com.dersaun.apicontas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResumoValorDTO {
    private BigDecimal valor;
    private BigDecimal porcentagem;
    private Boolean diminuiu;
}