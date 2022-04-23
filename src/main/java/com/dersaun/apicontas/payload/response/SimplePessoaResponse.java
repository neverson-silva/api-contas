package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dao.models.Pessoa;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SimplePessoaResponse implements Serializable {

    private Long id;
    private String nome;
    private String sobrenome;

    public SimplePessoaResponse(Pessoa pessoa) {
        id = pessoa.getId();
        nome = pessoa.getNome();
        sobrenome = pessoa.getSobrenome();
    }

    public SimplePessoaResponse(Long id, String nome, String sobrenome) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
    }
}
