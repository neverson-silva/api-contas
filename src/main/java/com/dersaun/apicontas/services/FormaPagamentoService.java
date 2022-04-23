package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.FormaPagamento;
import com.dersaun.apicontas.payload.request.AtualizarFormaPagamentoRequestDto;
import com.dersaun.apicontas.payload.request.CriarFormaPagamentoRequestDTO;
import com.dersaun.apicontas.payload.response.ListarTodosFormaPagamentoResponseDTO;
import org.eclipse.collections.api.list.MutableList;

public interface FormaPagamentoService {

    FormaPagamento findOne(Long id);

    MutableList<FormaPagamento> findAllByPermissao();

    FormaPagamento create(CriarFormaPagamentoRequestDTO dto);

    ListarTodosFormaPagamentoResponseDTO getAll();

    void atualizarStatus(Long id, Boolean ativo);

    void atualizar(Long id, AtualizarFormaPagamentoRequestDto dto);
}
