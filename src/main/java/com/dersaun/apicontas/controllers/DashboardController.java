package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.dto.MesAnoDTO;
import com.dersaun.apicontas.payload.response.CalculoResumoFaturaUsuarioResponse;
import com.dersaun.apicontas.payload.response.LineChartDataResponse;
import com.dersaun.apicontas.payload.response.ResumoFaturaPessoaResponse;
import com.dersaun.apicontas.services.DashboardService;
import com.dersaun.apicontas.services.MesService;
import com.dersaun.apicontas.services.ResumoFaturaService;
import com.dersaun.apicontas.services.UsuarioService;
import com.dersaun.apicontas.util.MesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dashboard")
public class DashboardController {

    @Autowired
    private ResumoFaturaService resumoFaturaService;

    @Autowired
    private MesService mesService;

    @Autowired
    private DashboardService dashboardService;

        @GetMapping("resumoUsuario/{pessoaId}")
    public ResponseEntity<CalculoResumoFaturaUsuarioResponse> resumoUsuario(@PathVariable("pessoaId") Long pessoaId  ) {

        Mes mesBusca = mesService.getMesAtual();
        MesAnoDTO mesAno = MesUtil.getMesAno(mesBusca);

        var resumoFaturaUsuarioResponse = dashboardService.montarResumoUsuario(UsuarioService.usuario(), mesAno);

        return ResponseEntity.ok(resumoFaturaUsuarioResponse);
    }

    @GetMapping("resumosFatura")
    public ResponseEntity<Map> resumosFatura(
            @RequestParam(name = "mes", required = false, defaultValue = "0") Long mes,
            @RequestParam(name = "ano", required = false, defaultValue = "0") Integer ano
    ) {
        Mes mesBusca = null;
        MesAnoDTO mesAno = null;

        if ((mes != null && mes > 0) && (ano != null && ano > 0)) {
            mesBusca = mesService.findById(mes);
            mesAno = MesUtil.getMesAno(mesBusca, ano);
        } else {
            mesBusca = mesService.getMesAtual();
            mesAno = MesUtil.getMesAno(mesBusca);
        }

        List<ResumoFaturaPessoaResponse> resumoFaturaUsuarioResponse = resumoFaturaService.calcularDespesas(mesAno);

        var response = new LinkedHashMap<>();

        response.put("title", "Resumos de " + mesAno.getMes().getNome() );
        response.put("mes", mesAno.getMes().getId());
        response.put("ano", mesAno.getAnoReferencia());
        response.put("dados", resumoFaturaUsuarioResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("resumosPessoas/lineChart")
    public ResponseEntity<LineChartDataResponse> lineChart(
            @RequestParam(value = "numeroMeses", required = false, defaultValue = "4") Integer numeroMeses) {
//    public ResponseEntity<LineChartDataResponse> lineChart() {
        var mesesAnosComSomaPessoa = dashboardService.somarValoresCadaPessoaMesesAnteriores(numeroMeses, true);

        var lineChartData = dashboardService.buildDatasetsLineChart(mesesAnosComSomaPessoa);
        return ResponseEntity.ok(lineChartData);
    }

}
