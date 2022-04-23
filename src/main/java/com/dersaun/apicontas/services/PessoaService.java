package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.Pessoa;
import org.eclipse.collections.api.list.MutableList;

public interface PessoaService extends Service<Pessoa, Long>{

    MutableList<?> findAll(Boolean simple);

    Pessoa findById(long id);
}
