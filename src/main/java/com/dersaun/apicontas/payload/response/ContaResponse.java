package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dao.models.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContaResponse {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private Integer ano;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dataCompra;
    private Boolean parcelado;
    private Integer quantidadeParcelas;
    private Mes mes;
    private FormaPagamento formaPagamento;
    private Pessoa pessoa;
    private Boolean pago;
    private Integer tipoConta;
    private Integer divisaoId;
    private BigDecimal valorDividido;
    //private Set<ParcelaResponse> parcelas;
    private Boolean readOnly;
    private BigDecimal valorUtilizado;
    private Boolean dividido;
    private BigDecimal valorPorParcela;

    public ContaResponse(FaturaItem faturaItem) {
        buildInstance(faturaItem.getLancamento());
    }
    public ContaResponse(Lancamento lancamento) {
        buildInstance(lancamento);
    }

    private void buildInstance(Lancamento lancamento) {
        id = lancamento.getId();
        nome = lancamento.getNome();
        descricao = lancamento.getDescricao();
        valor = lancamento.getValor();
        ano = lancamento.getAno();
        dataCompra = lancamento.getDataCompra();
        parcelado = lancamento.isParcelado();
        quantidadeParcelas = lancamento.getQuantidadeParcelas();
        mes = lancamento.getMes();
        pago = lancamento.isPago();
        tipoConta = lancamento.getTipoConta();
        divisaoId = lancamento.getDivisaoId();
        valorDividido = lancamento.getValorDividido();
        dividido = lancamento.isDividido();
        valorPorParcela = lancamento.getValorPorParcela();
        //parcelas = conta.getParcelas().stream().map(ParcelaResponse::new).collect(Collectors.toSet());
        pessoa = lancamento.getPessoa();
        formaPagamento = lancamento.getFormaPagamento();

    }

}
