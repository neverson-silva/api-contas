package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.payload.request.AtualizacaoPerfilRequest;
import com.dersaun.apicontas.services.PerfilService;
import com.dersaun.apicontas.services.PessoaService;
import com.dersaun.apicontas.services.StorageService;
import com.dersaun.apicontas.services.UsuarioService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PerfilServiceImpl implements PerfilService {

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private Logger log;

    @Override
    public Pessoa atualizarPerfil(Long pessoaId, AtualizacaoPerfilRequest params) {
        var pessoa = pessoaService.findById(pessoaId.longValue());
        var usuarioOpt = usuarioService.findByPessoa(pessoa);
        pessoa.setNome(params.getNome());
        pessoa.setSobrenome(params.getSobrenome());
        pessoa.setApelido(params.getApelido());
        pessoa.setDataNascimento(params.getDataNascimento());

        if (params.getFotoPerfil() != null) {
            String url = null;
            try {
                url = storageService.upload(params.getFotoPerfil(), pessoa.getNome());
            } catch (IOException e) {
                log.error("Erro ao enviar arquivo ao storage");
            }
            pessoa.setPerfil(url);
        }

        if (usuarioOpt.isPresent()) {
            var usuario = usuarioOpt.get();
            usuario.setConta(params.getEmail());
            usuarioService.save(usuario);
        }
        pessoaService.save(pessoa);
        return pessoa;
    }
    public Pessoa atualizarFotoPerfil(long pessoaId, MultipartFile fotoPerfil) throws IOException {
        var pessoa = pessoaService.findById(pessoaId);
        var url = storageService.upload(fotoPerfil, pessoa.getNome());
        pessoa.setPerfil(url);
        pessoaService.save(pessoa);
        return pessoa;
    }

    @Override
    public Object obterDadosPerfil(Long pessoaId) {
        return null;
    }
}
