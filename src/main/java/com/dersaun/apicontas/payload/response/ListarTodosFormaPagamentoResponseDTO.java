package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dao.models.FormaPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.list.MutableList;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListarTodosFormaPagamentoResponseDTO implements Serializable {

    private MutableList<FormaPagamento> ativos;
    private MutableList<FormaPagamento> cancelados;
}
