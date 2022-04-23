package com.dersaun.apicontas.dao.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Data
public class ResumoFaturaId implements Serializable {

    @Column(name = "pessoa_id", insertable = false, updatable = false)
    private Long devedorId;

    @Column(name = "cartao_id", insertable = false, updatable = false)
    private Long formaPagamentoId;

    @Column(name = "mes_id", insertable = false, updatable = false)
    private Long mesId;

    private Integer ano;

    public ResumoFaturaId(Pessoa pessoa, FormaPagamento formaPagamento) {
        this.devedorId = pessoa.getId();
        this.formaPagamentoId = formaPagamento.getId();
    }

    public ResumoFaturaId() {

    }

    public ResumoFaturaId(Long devedorId, Long formaPagamentoId, Long mesId, Integer ano) {
        this.devedorId = devedorId;
        this.formaPagamentoId = formaPagamentoId;
        this.mesId = mesId;
        this.ano = ano;
    }
}