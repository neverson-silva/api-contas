package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dto.ResumoValorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumoFaturaUsuarioResponse implements Serializable {

    private Long pessoaId;
    private ResumoValorDTO aVista;
    private ResumoValorDTO parcelado;
    private ResumoValorDTO dinheiro;
    private ResumoValorDTO total;
}


