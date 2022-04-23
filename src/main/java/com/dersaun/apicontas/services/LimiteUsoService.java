package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.LimiteUso;
import com.dersaun.apicontas.payload.request.LimiteUsoRequest;
import com.dersaun.apicontas.payload.response.SimplePessoaResponse;
import org.eclipse.collections.api.list.MutableList;
import org.springframework.data.domain.Sort;

public interface LimiteUsoService extends Service<LimiteUso, Long> {

    MutableList<LimiteUso> findAll(Sort sort);
    MutableList<LimiteUso> findAll();
    LimiteUso findOne(Long id);
    LimiteUso findByPessoaId(Long id);
    LimiteUso update(Long id, LimiteUsoRequest params);
    LimiteUso create(LimiteUsoRequest dto);
    void deleteById(Long id);

    MutableList<SimplePessoaResponse> getPessoasSemCadastro();
}
