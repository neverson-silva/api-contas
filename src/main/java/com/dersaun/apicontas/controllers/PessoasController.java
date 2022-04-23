package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.services.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("pessoas")
public class PessoasController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping("showAll")
    public ResponseEntity<List<?>> showAll(@RequestParam(value = "simple", defaultValue = "false", required = false)
                                                 Boolean simple) {
        var pessoas = pessoaService.findAll(simple);

        return ResponseEntity.ok(pessoas);
    }
}
