package com.dersaun.apicontas.contracts;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dao.models.FormaPagamento;
import com.dersaun.apicontas.dao.models.Parcela;
import com.dersaun.apicontas.dao.models.Pessoa;

import java.io.Serializable;
import java.math.BigDecimal;

public interface Faturavel extends Serializable {

    Lancamento getLancamento();

    Pessoa getPessoa();

    Parcela getParcela();

    FormaPagamento getFormaPagamento();

    BigDecimal getValorUtilizado();

    Boolean isParcelado();

    Boolean isDividido();

    Boolean isPrincipal();

    Divisivel getPrincipal();
}