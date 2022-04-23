package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.dao.models.LimiteUso;
import com.dersaun.apicontas.payload.request.LimiteUsoRequest;
import com.dersaun.apicontas.payload.response.SimplePessoaResponse;
import com.dersaun.apicontas.services.LimiteUsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dersaun.apicontas.exceptions.StandardHttpException.notFound;

@RestController
@RequestMapping("limiteUso")
public class LimiteUsoController {

    @Autowired
    private LimiteUsoService limiteUsoService;

    @GetMapping("")
    public ResponseEntity<List<LimiteUso>> index() {
        var limites = limiteUsoService.findAll(Sort.by(Sort.Direction.ASC, "pessoa.nome"));
        return ResponseEntity.ok(limites);
    }

    @GetMapping("pessoasSemLimiteUso")
    public ResponseEntity<List<SimplePessoaResponse>> showAllPessoas() {
        var limites = limiteUsoService.getPessoasSemCadastro();
        return ResponseEntity.ok(limites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LimiteUso> show(@PathVariable("id") Long id) {
        var limite = limiteUsoService.findOne(id);
        return ResponseEntity.ok(limite);
    }

    @PostMapping("")
    public ResponseEntity<LimiteUso> create(@RequestBody LimiteUsoRequest params) {
        var limite = limiteUsoService.create(params);
        return ResponseEntity.ok(limite);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id,
                                          @RequestBody LimiteUsoRequest params) {
        var limite = limiteUsoService.update(id, params);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("buscarPorPessoa/{id}")
    public ResponseEntity<LimiteUso> buscarPorPessoa(@PathVariable("id") Long id) {
        var limite = limiteUsoService.findByPessoaId(id);
        if (limite == null) {
            throw notFound("Limite não cadastrado");
        }
        return ResponseEntity.ok(limite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        limiteUsoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
