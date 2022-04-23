package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dto.LancamentoDTO;
import com.dersaun.apicontas.payload.response.ContaResponse;
import com.dersaun.apicontas.services.LancamentoService;
import com.dersaun.apicontas.util.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoService lancamentoService;

    @PostMapping("")
    public ResponseEntity<?> save(@Valid() @RequestBody() LancamentoDTO lancamentoDTO, HttpServletRequest req) {
        Lancamento lancamento = lancamentoService.criarLancamento(lancamentoDTO);

        URI uri = Commons.createFromUri(lancamento.getId().intValue(), "lancamentos/{id}", req);

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> show(@PathVariable("id") Long id) {
        var conta = lancamentoService.getOne(id);
        return ResponseEntity.ok(new ContaResponse(conta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        lancamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
