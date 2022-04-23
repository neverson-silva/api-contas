package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.services.MesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("meses")
public class MesController {

    @Autowired
    private MesService mesService;

    @GetMapping("mesAtual")
    private ResponseEntity<Mes> mesAtual() {
        var mesAtual = mesService.getMesAtual();
        return ResponseEntity.ok(mesAtual);
    }
}
