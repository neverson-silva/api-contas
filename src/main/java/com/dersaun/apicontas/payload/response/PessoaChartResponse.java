package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dao.models.Pessoa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaChartResponse implements Serializable {

    private Long id;
    private String nome;
    private String sobrenome;
    private String nomeCompleto;

    public PessoaChartResponse(Pessoa pessoa) {
        id = pessoa.getId();
        nome = pessoa.getNome();
        sobrenome = pessoa.getSobrenome();
        nomeCompleto = pessoa.getNomeCompleto();
    }
}
