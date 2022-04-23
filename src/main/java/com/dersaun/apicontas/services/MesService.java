package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.FormaPagamento;
import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.dto.MesAnoDTO;

import java.time.LocalDate;

public interface MesService {

    Mes getMesAtual();

    MesAnoDTO getCurrent();

    Mes findById(Long mes);

    Mes getFromDataCompraAndFormaPagamento(LocalDate dataCompra, FormaPagamento formaPagamento);
}
