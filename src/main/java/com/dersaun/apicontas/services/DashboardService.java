package com.dersaun.apicontas.services;

import com.dersaun.apicontas.contracts.UserCredentialDetails;
import com.dersaun.apicontas.dto.MesAnoChart;
import com.dersaun.apicontas.dto.MesAnoDTO;
import com.dersaun.apicontas.payload.response.CalculoResumoFaturaUsuarioResponse;
import com.dersaun.apicontas.payload.response.LineChartDataResponse;
import org.eclipse.collections.api.list.MutableList;

public interface DashboardService {

    MutableList<MesAnoChart> somarValoresCadaPessoaMesesAnteriores(Integer numeroMeses, Boolean contarAtual);

    LineChartDataResponse buildDatasetsLineChart(MutableList<MesAnoChart> mesesAnos);

    CalculoResumoFaturaUsuarioResponse montarResumoUsuario(UserCredentialDetails usuario, MesAnoDTO mesAno);
}
