package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.dao.models.FormaPagamento;
import com.dersaun.apicontas.payload.request.AtualizarFormaPagamentoRequestDto;
import com.dersaun.apicontas.payload.request.AtualizarStatusRequestDto;
import com.dersaun.apicontas.payload.request.CriarFormaPagamentoRequestDTO;
import com.dersaun.apicontas.payload.response.ListarTodosFormaPagamentoResponseDTO;
import com.dersaun.apicontas.services.FormaPagamentoService;
import org.eclipse.collections.api.list.MutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("formasPagamento")
public class FormaPagamentoController {

    @Autowired
    private FormaPagamentoService formaPagamentoService;

    @GetMapping("")
    public ResponseEntity<MutableList<?>> index() {

        var formasPagamento = formaPagamentoService.findAllByPermissao();

        return ResponseEntity.ok(formasPagamento);
    }

    @GetMapping("getAll")
    public ResponseEntity<ListarTodosFormaPagamentoResponseDTO> getAll() {
        var formasPagamento = formaPagamentoService.getAll();

        return ResponseEntity.ok(formasPagamento);
    };

    @PutMapping("atualizarStatus/{id}")
    public ResponseEntity<Void> atualizarStatus(@PathVariable("id") Long id, @RequestBody() AtualizarStatusRequestDto dto) {
        formaPagamentoService.atualizarStatus(id, dto.getAtivo());
        return ResponseEntity.noContent().build();
    }
    @PutMapping("{id}")
    public ResponseEntity<Void> atualizarStatus(@PathVariable("id") Long id, @RequestBody() AtualizarFormaPagamentoRequestDto dto) {
        formaPagamentoService.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody CriarFormaPagamentoRequestDTO dto) {
        var formaPagamento = formaPagamentoService.create(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                            .path("/{id}")
                                            .buildAndExpand(formaPagamento.getId())
                                            .toUri();
        return ResponseEntity.created(uri).build();
    }
}
