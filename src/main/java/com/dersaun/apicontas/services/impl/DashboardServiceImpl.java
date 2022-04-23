package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.contracts.UserCredentialDetails;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.models.ResumoFatura;
import com.dersaun.apicontas.dao.repositories.PessoaCorGraficoRepository;
import com.dersaun.apicontas.dto.DatasetDTO;
import com.dersaun.apicontas.dto.MesAnoChart;
import com.dersaun.apicontas.dto.MesAnoDTO;
import com.dersaun.apicontas.payload.response.CalculoResumoFaturaUsuarioResponse;
import com.dersaun.apicontas.payload.response.LineChartDataResponse;
import com.dersaun.apicontas.services.*;
import com.dersaun.apicontas.util.MesUtil;
import com.dersaun.apicontas.util.NumberUtil;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private MesService mesService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ResumoFaturaService resumoFaturaService;

    @Autowired
    private PessoaCorGraficoRepository pessoaCorGraficoRepositorio;


    @Override
    public CalculoResumoFaturaUsuarioResponse montarResumoUsuario(UserCredentialDetails usuario, MesAnoDTO mesAno) {
        return resumoFaturaService.calcularResumoUsuario(usuario.getPessoa(), mesAno);
    }

    @Override
    @Transactional(readOnly = true)
    public MutableList<MesAnoChart> somarValoresCadaPessoaMesesAnteriores(Integer numeroMeses, Boolean contarAtual) {

        MutableList<MesAnoChart> mesesAnos = getLastMonths(numeroMeses, contarAtual);
        Pessoa usuarioPessoa = !UsuarioService.usuario().isAdmin() ? UsuarioService.usuario().getPessoa() : null;
        MutableList<ResumoFatura> resumos = resumoFaturaService.getFaturasUltimosMeses(mesesAnos, usuarioPessoa);
        MutableMap<Pessoa, RichIterable<ResumoFatura>> pessoas = resumos.groupBy(ResumoFatura::getPessoa).toMap();

        mesesAnos.forEach(mesAno -> {
            MutableMap<Pessoa, BigDecimal> lancs = Maps.mutable.empty();

            var resumosFatura = resumos.select(
                    res -> res.getMes().getId().equals(mesAno.getFechamento()) &&
                           res.getAno().equals(mesAno.getAnoFechamento())
            ).sortThis(Comparator.comparing(a -> a.getPessoa().getId()))
             .groupBy(ResumoFatura::getPessoa)
             .toMap();

            resumosFatura.forEach((pessoa, rest) -> {
                var totalPessoa = NumberUtil.roundValue(
                        rest.sumOfDouble(r -> r.getValorTotal().doubleValue() )
                );
                lancs.put(pessoa, totalPessoa);
            }) ;

            Set<Pessoa> pessoasLancs = lancs.keySet();

            var pessoasNaoExiste = pessoas.keySet()
                                                        .stream()
                                                        .filter(p ->  !pessoasLancs.contains(p) )
                                                        .collect(Collectors.toList());
            if (!pessoasNaoExiste.isEmpty()) {
                pessoasNaoExiste.forEach( p -> lancs.put(p, BigDecimal.valueOf(0)));
            }
            mesAno.setLancamentos(lancs);
        });
        return mesesAnos;
    }

    @Override
    @Transactional(readOnly = true)
    public LineChartDataResponse buildDatasetsLineChart(MutableList<MesAnoChart> mesesAnos) {

        MutableList<String> labels = mesesAnos.collect(mes -> (mes.getFechamento().intValue() <= 9 ? "0" : "")  + mes.getFechamento() + "/" + mes.getAno() );
        MutableList<String> legends = new FastList<>();

        MutableMap<Pessoa, MutableList<BigDecimal>> pessoas = Maps.mutable.empty();

        mesesAnos.forEach(mesAno -> {
            mesAno.getLancamentos().forEach((pessoa, total) -> {
                if(!pessoas.containsKey(pessoa)) {
                    MutableList<BigDecimal> totalList = new FastList<>();
                    totalList.add(total);
                    pessoas.put(pessoa, totalList);
                }else {
                    pessoas.get(pessoa).add(total);
                }
            });
        });

        MutableList<DatasetDTO> datasets = buildDatasets(pessoas, legends);

        return new LineChartDataResponse(legends, labels, datasets);
    }

    private MutableList<MesAnoChart> getLastMonths(Integer numeroMeses, Boolean contarAtual) {
        var mesAtual = mesService.getMesAtual();

        var hoje = LocalDateTime.now();

        MutableList<MesAnoChart> mesesAnos = new FastList<>();

        int ano = mesAtual.getId() == 12L && hoje.getMonthValue() == 1 ? hoje.getYear() - 1 : hoje.getYear();

        MesAnoDTO label = null;

        if (contarAtual) {
            label = new MesAnoDTO(mesAtual.getId(), ano);
        } else {
            label = MesUtil.getMesAnoAnteriores(mesAtual.getId(), ano);
        }
        for (int numero = 1; numero <= numeroMeses; numero++) {
            MesAnoChart mesAno = new MesAnoChart();
            mesAno.setId(label.getMesReferencia());
            mesAno.setAno(label.getAnoReferencia());
            mesAno.setNome(MesUtil.nome(label.getMesReferencia()));
            mesAno.setFechamento(label.getMesReferencia());
            mesAno.setAnoFechamento(label.getAnoReferencia());
            mesesAnos.add(mesAno);
            label = MesUtil.getMesAnoAnteriores(label.getMesReferencia(), label.getAnoReferencia());
        }

        return mesesAnos.reverseThis();

    }

    private MutableList<DatasetDTO> buildDatasets(MutableMap<Pessoa, MutableList<BigDecimal>> pessoas,
                                                  MutableList<String> legends ) {

        var pessoaCores = pessoaCorGraficoRepositorio.obterTudo();

        var dataSets =  pessoas
                                            .entrySet()
                                            .stream()
                                            .sorted(Comparator.comparing(a -> a.getKey().getNome()))
                                            .map(entry -> {
                                                var pessoa = entry.getKey();
                                                var data = entry.getValue();
                                                var pessoaCor = pessoaCores.stream()
                                                        .filter(p -> p.getPessoa().equals(pessoa)).collect(Collectors.toList()).get(0);
                                                DatasetDTO dataset = new DatasetDTO();
                                                dataset.setColor(pessoaCor.getBackground());
                                                dataset.setData(data);

                                                legends.add(pessoa.getNome());
                                                return dataset;
                                            }).collect(Collectors.toList());
        return new FastList<>(dataSets);
    }
}
