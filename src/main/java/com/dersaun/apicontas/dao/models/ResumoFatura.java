package com.dersaun.apicontas.dao.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"dono"})
public class ResumoFatura {

    @EmbeddedId
    @JsonIgnore
    private ResumoFaturaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "pessoa_id", insertable = false, updatable = false)
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "cartao_id", insertable = false, updatable = false)
    private FormaPagamento formaPagamento;

    private BigDecimal valorTotal;

    private BigDecimal valorParcelado;

    @Column(name = "valor_a_vista")
    private BigDecimal valorAVista;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mes_id", insertable = false, updatable = false)
    private Mes mes;

    private BigDecimal saldo;

    public ResumoFatura(Pessoa pessoa,FormaPagamento formaPagamento,
                        BigDecimal valorTotal, Mes mes, Integer ano, BigDecimal saldo) {
        this.valorTotal = valorTotal;
        this.mes = mes;
        this.saldo = saldo;
        this.id = new ResumoFaturaId(pessoa, formaPagamento);
        this.id.setAno(ano);
        this.id.setMesId(mes.getId());
    }

    public ResumoFatura(Pessoa pessoa,FormaPagamento formaPagamento, Long mes, Integer ano) {
        this.id = new ResumoFaturaId(pessoa, formaPagamento);
        this.id.setMesId(mes);
        this.id.setAno(ano);
    }

    public void setId(Pessoa pessoa, FormaPagamento formaPagamento) {
        this.id = new ResumoFaturaId(pessoa, formaPagamento);
    }

    public Integer getAno() {
        return this.id.getAno();
    }

}