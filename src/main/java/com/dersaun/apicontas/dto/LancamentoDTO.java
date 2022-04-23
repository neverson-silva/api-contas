package com.dersaun.apicontas.dto;

import com.dersaun.apicontas.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LancamentoDTO implements Serializable {

    @Max(value = 50L, message = "Descrição deve conter no máximo 50 caracteres")
    private String nome;

    @NotBlank(message = "Valor é obrigatório")
    private BigDecimal valor;

    private String descricao;

    private Long pessoaId;

    private Boolean parcelado;

    private Boolean contaFixa;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dataCompra;

    @NotBlank(message = "É obrigatório informar a forma de pagamento")
    private Long formaPagamentoId;

    private Integer quantidadeParcelas;

    private List<Long> pessoasDivididasIgualmente;

    private List<PessoasDivisaoDiferenteDTO> pessoasDivididasDiferente;

    public Boolean isIgual() {
        return pessoasDivididasIgualmente != null && pessoasDivididasIgualmente.size() > 0;
    }

    public Boolean isDiferente() {
        return pessoasDivididasDiferente != null && pessoasDivididasDiferente.size() > 0;
    }

    public Boolean isDividido() {
        return isIgual() || isDiferente();
    }

    public Boolean isContaFixa() {
        return contaFixa != null && contaFixa;
    }

    public MutableList<Long> getPessoasDivididasIgualmente() {
        if (pessoasDivididasIgualmente == null) {
            pessoasDivididasIgualmente = new FastList<>();
        }
        return new FastList<>(pessoasDivididasIgualmente);
    }

    public MutableList<PessoasDivisaoDiferenteDTO> getPessoasDivididasDiferente() {
        if (pessoasDivididasDiferente == null) {
            pessoasDivididasDiferente = new FastList<>();
        }
        return new FastList<>(pessoasDivididasDiferente);
    }

    public BigDecimal getTotalPessoasDivididoDiferente() {
        return NumberUtil.somar(getPessoasDivididasDiferente()
                .collect(p -> p.getValor().doubleValue()));
    }

}
