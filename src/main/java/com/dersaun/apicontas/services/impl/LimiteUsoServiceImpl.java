package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.LimiteUso;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.repositories.LimiteUsoRepository;
import com.dersaun.apicontas.dao.repositories.PessoaRepository;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.payload.request.LimiteUsoRequest;
import com.dersaun.apicontas.payload.response.SimplePessoaResponse;
import com.dersaun.apicontas.services.LimiteUsoService;
import com.dersaun.apicontas.services.UsuarioService;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dersaun.apicontas.exceptions.StandardHttpException.notFound;
@Service
public class LimiteUsoServiceImpl extends SimpleServiceImpl<LimiteUso, Long> implements LimiteUsoService {

    @Autowired
    private LimiteUsoRepository repository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Override
    @Transactional(readOnly = true)
    public LimiteUso findOne(Long id) {
        return repository.findById(id)
                    .orElseThrow(() -> notFound("Limite não encontrado para o id: " + id));
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public LimiteUso update(Long id, LimiteUsoRequest params) {
        var limiteUso = findOne(id);
        limiteUso.setLimite(params.getLimite());
        limiteUso.setPessoa(new Pessoa(params.getPessoaId()));
        return update(limiteUso);
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public LimiteUso create(LimiteUsoRequest dto) {
        var limiteUso = new LimiteUso();
        limiteUso.setLimite(dto.getLimite());
        limiteUso.setPessoa(new Pessoa(dto.getPessoaId()));

        return save(limiteUso);
    }

    @Override
    @Transactional(readOnly = true)
    public LimiteUso findByPessoaId(Long id) {
        return repository.findByPessoaId(id);
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public void deleteById(Long id) {
        var limiteUso = findOne(id);
        delete(limiteUso);
    }

    @Override
    public MutableList<SimplePessoaResponse> getPessoasSemCadastro() {
        var pessoas = pessoaRepository.findAllSemLimiteUso();
        return pessoas.collect(SimplePessoaResponse::new);
    }

    @Override
    public MutableList<LimiteUso> findAll(Sort sort) {
        var usuario = UsuarioService.usuario();
        if (usuario.isAdmin()) {
            return super.findAll(sort);
        }
        var limitePessoa = repository.findByPessoaId(usuario.getPessoa().getId());
        return limitePessoa != null ? FastList.newListWith(limitePessoa) : new FastList<>();
    }

    @Override
    public JpaRepository<LimiteUso, Long> getRepository() {
        return repository;
    }
}
