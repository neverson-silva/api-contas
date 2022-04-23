package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.payload.request.AtualizacaoPerfilRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PerfilService {

    Pessoa atualizarPerfil(Long pessoaId, AtualizacaoPerfilRequest params);

    Pessoa atualizarFotoPerfil(long pessoaId, MultipartFile fotoPerfil) throws IOException;

    public Object obterDadosPerfil(Long pessoaId);
}
