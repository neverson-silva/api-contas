package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.models.Usuario;
import com.dersaun.apicontas.dao.repositories.RegraRepository;
import com.dersaun.apicontas.dao.repositories.UsuarioRepository;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.payload.request.UserCreateRequest;
import com.dersaun.apicontas.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UsuarioServiceImpl extends SimpleServiceImpl<Usuario, Long>  implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RegraRepository regraRepository;

    @Override
    @Transactional
    public void salvar(UserCreateRequest user) {
        var userValidate = usuarioRepository.findByConta(user.getConta());

        if (userValidate.isPresent()) {
            throw StandardHttpException.badRequest("Usuário já existe", "Usuário " + user.getConta() + " já cadastrado no sistema" );
        }

        var pessoa = new Pessoa();
        pessoa.setId(user.getPessoaId());

        var usuario = new Usuario();

        usuario.setConta(user.getConta());
        usuario.setSenha(user.getSenha());
        usuario.setPessoa(pessoa);
        usuario.setSituacao(1);
        usuario.setPrimeiroAcesso(true);

        var regra = regraRepository.findByAuthority("ROLE_USUARIO");

        usuario.setRegras(Collections.singletonList(regra));

        usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findByPessoa(Pessoa pessoa) {
        return usuarioRepository.findByPessoa(pessoa);
    }

    @Override
    public JpaRepository<Usuario, Long> getRepository() {
        return usuarioRepository;
    }
}
