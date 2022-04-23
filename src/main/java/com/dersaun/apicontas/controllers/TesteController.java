package com.dersaun.apicontas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TesteController {

    @GetMapping("")
    public ResponseEntity<?> teste() {
        return ResponseEntity.ok("Hello World autenticado");
    }
}
