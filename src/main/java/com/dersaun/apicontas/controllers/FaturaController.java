package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.dao.models.FaturaItem;
import com.dersaun.apicontas.services.FaturaService;
import com.dersaun.apicontas.util.MesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("faturas")

public class FaturaController {

    @Autowired
    private FaturaService faturaService;

    @GetMapping("faturaAtual")
    public ResponseEntity<Page<FaturaItem>> lancamentosAtuais(
            @RequestParam(value="page", defaultValue="1") Integer page,
            @RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
            @RequestParam(value="orderBy", defaultValue="lancamento.dataCompra") String orderBy,
            @RequestParam(value="direction", defaultValue="DESC") String direction

    ) {
        var lancamentos =  faturaService.findLancamentosFaturaAtual(page, linesPerPage,orderBy, direction, null);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("buscarFaturas")
    public ResponseEntity<Page<FaturaItem>> buscarFaturas(
            @RequestParam(value = "pessoaId") Long pessoaId,
            @RequestParam(value = "mes") Long mes,
            @RequestParam(value = "ano") Integer ano,
            @RequestParam(value="page", defaultValue="1") Integer page,
            @RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
            @RequestParam(value="orderBy", defaultValue="lancamento.dataCompra") String orderBy,
            @RequestParam(value="direction", defaultValue="DESC") String direction
    ) {
        var lancamentos = faturaService.findLancamentosFatura(MesUtil.getMesAnoReferencia(mes, ano), page, linesPerPage, orderBy, direction, false, pessoaId);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("show")
    public ResponseEntity<FaturaItem> show(@RequestParam("contaId") Long contaId,
                                           @RequestParam(value = "mes", required = false, defaultValue = "0") Long mes,
                                           @RequestParam(value = "ano", required = false, defaultValue = "0") Integer ano
                                           ) {
        var lancamento = ano > 0 ? faturaService.findByContaIdMesAndAno(contaId, mes, ano) : faturaService.findByContaIdAtual(contaId);
        return ResponseEntity.ok(lancamento);
    }

    @GetMapping("buscar")
    public ResponseEntity<Page<FaturaItem>> buscarFaturas(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value="page", defaultValue="1") Integer page,
            @RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
            @RequestParam(value="orderBy", defaultValue="lancamento.dataCompra") String orderBy,
            @RequestParam(value="direction", defaultValue="DESC") String direction,
            @RequestParam(value = "mes", defaultValue = "0", required = false) long mes,
            @RequestParam(value = "ano", defaultValue = "0", required = false) int ano
    ) {
        var lancamentos = faturaService.findAllByKeyword(keyword, page, linesPerPage, orderBy, direction, mes, ano);
        return ResponseEntity.ok(lancamentos);
    }
}
