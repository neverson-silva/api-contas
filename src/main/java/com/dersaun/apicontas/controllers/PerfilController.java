package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.payload.request.AtualizacaoPerfilRequest;
import com.dersaun.apicontas.services.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("perfis")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @RequestMapping(path = "/{pessoaId}", method = RequestMethod.PUT,
            consumes = {"multipart/form-data"})
    public ResponseEntity<?> atualizarPerfil( @ModelAttribute AtualizacaoPerfilRequest params,
                                @PathVariable Long pessoaId) throws IOException {
        var pessoa = perfilService.atualizarPerfil(pessoaId, params);

        return ResponseEntity.ok(pessoa.getPerfil());
    }

    @RequestMapping(path = "/fotoPerfil/{pessoaId}", method = RequestMethod.PUT,
            consumes = {"multipart/form-data"})
    public ResponseEntity<?> atualizarFotoPerfil(@RequestParam MultipartFile fotoPerfil,
                                                 @PathVariable Long pessoaId) throws IOException {
        perfilService.atualizarFotoPerfil(pessoaId, fotoPerfil);

        return ResponseEntity.noContent().build();
    }
}
