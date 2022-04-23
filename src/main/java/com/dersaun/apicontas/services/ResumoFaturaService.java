package com.dersaun.apicontas.services;

import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.models.ResumoFatura;
import com.dersaun.apicontas.dto.MesAnoChart;
import com.dersaun.apicontas.dto.MesAnoDTO;
import com.dersaun.apicontas.payload.response.CalculoResumoFaturaUsuarioResponse;
import com.dersaun.apicontas.payload.response.ResumoFaturaPessoaResponse;
import com.dersaun.apicontas.payload.response.ResumoFaturaUsuarioResponse;
import org.eclipse.collections.api.list.MutableList;

public interface ResumoFaturaService {

    ResumoFaturaUsuarioResponse calcularDespesasUsuario(Long pessoaId, MesAnoDTO mesAnoDTO);

    CalculoResumoFaturaUsuarioResponse calcularResumoUsuario(Pessoa pessoa, MesAnoDTO mesAnoDTO);

    MutableList<ResumoFaturaPessoaResponse> calcularDespesas(MesAnoDTO mesAno);

    MutableList<ResumoFatura> getFaturasUltimosMeses(MutableList<MesAnoChart> mesesAnos, Pessoa usuarioPessoa);

//    List<ResumoFatura> findAllByMesAndAno(Long mes, Integer ano);
}
