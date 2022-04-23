package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.repositories.PessoaRepository;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.payload.response.SimplePessoaResponse;
import com.dersaun.apicontas.services.PessoaService;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PessoaServiceImpl extends SimpleServiceImpl<Pessoa, Long>  implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Override
    public MutableList<?> findAll(Boolean simple) {
        var order = Sort.by(Sort.Direction.ASC, "nome");
        var pessoas =  new FastList<>(pessoaRepository.findAll(order));

        if (simple) {
            return pessoas.collect(SimplePessoaResponse::new);
        }
        return pessoas;
    }

    @Override
    public JpaRepository<Pessoa, Long> getRepository() {
        return pessoaRepository;
    }

    @Override
    public Pessoa findById(long id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> StandardHttpException.notFound("Não encontrado", "Pessoa com id " + id + " não encontrada."));
    }
}
