package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.contracts.UserCredentialDetails;
import com.dersaun.apicontas.dao.models.LimiteUso;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.models.ResumoFatura;
import com.dersaun.apicontas.dao.repositories.ResumoFaturaRepository;
import com.dersaun.apicontas.dto.MesAnoChart;
import com.dersaun.apicontas.dto.MesAnoDTO;
import com.dersaun.apicontas.dto.ResumoValorDTO;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.payload.response.CalculoResumoFaturaUsuarioResponse;
import com.dersaun.apicontas.payload.response.ResumoFaturaPessoaResponse;
import com.dersaun.apicontas.payload.response.ResumoFaturaUsuarioResponse;
import com.dersaun.apicontas.payload.response.ValorFormaPagamentoResponse;
import com.dersaun.apicontas.services.LimiteUsoService;
import com.dersaun.apicontas.services.ResumoFaturaService;
import com.dersaun.apicontas.services.UsuarioService;
import com.dersaun.apicontas.util.MesUtil;
import com.dersaun.apicontas.util.NumberUtil;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ResumoFaturaServiceImpl implements ResumoFaturaService {

    @Autowired
    private ResumoFaturaRepository resumoFaturaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private LimiteUsoService limiteUsoService;

    @Value("${OUTRO_NOME_FORMA_PAGAMENTO}")
    private String outroNome;

    public static final long DINHEIRO = 7L;
    public static final long CARNE = 6L;

    @Override
    public ResumoFaturaUsuarioResponse calcularDespesasUsuario(Long pessoaId, MesAnoDTO mesAnoDTO) {

        MutableList<ResumoFatura> resumos = resumoFaturaRepository.findAllByPessoaIdAndMesAndAno(
                pessoaId, mesAnoDTO.getMesReferencia(), mesAnoDTO.getAnoReferencia());
        if (resumos.isEmpty()) {
            throw StandardHttpException.notFound("Nenhum resumo encontado para usuário, no mês atual");
        }

        var mesAnterior = MesUtil.getMesAnoAnterior(mesAnoDTO);
        var resumosAnteriores = resumoFaturaRepository.findAllByPessoaIdAndMesAndAno(
                pessoaId, mesAnterior.getMesReferencia(), mesAnterior.getAnoReferencia()
        );
        var parcelado = somarValorParcelado(resumos);
        var vista = somarValorVista(resumos);
        var dinheiro = somarValorDinheiro(resumos);
        var total = NumberUtil.roundValue(parcelado.doubleValue() + vista.doubleValue() + dinheiro.doubleValue());

        var parceladoAnterior = somarValorParcelado(resumosAnteriores);
        var vistaAnterior = somarValorVista(resumosAnteriores);
        var dinheiroAnterior = somarValorDinheiro(resumosAnteriores);
        var totalAnterior = NumberUtil.roundValue(parceladoAnterior.doubleValue() + vistaAnterior.doubleValue() + dinheiroAnterior.doubleValue());

        var resumoResponse = new ResumoFaturaUsuarioResponse();
        resumoResponse.setPessoaId(pessoaId);
        resumoResponse.setParcelado(getResumoValorDTO(parcelado, parceladoAnterior));
        resumoResponse.setAVista(getResumoValorDTO(vista, vistaAnterior));
        resumoResponse.setDinheiro(getResumoValorDTO(dinheiro, dinheiroAnterior));
        resumoResponse.setTotal(getResumoValorDTO(total, totalAnterior));

        return resumoResponse;
    }

    @Override
    public CalculoResumoFaturaUsuarioResponse calcularResumoUsuario(Pessoa pessoa, MesAnoDTO mesAnoDTO) {

        MutableList<ResumoFatura> resumos = resumoFaturaRepository.findAllByPessoaIdAndMesAndAno(
                pessoa.getId(), mesAnoDTO.getMesReferencia(), mesAnoDTO.getAnoReferencia());

        var mesAnterior = MesUtil.getMesAnoAnterior(mesAnoDTO);
        var resumosAnteriores = resumoFaturaRepository.findAllByPessoaIdAndMesAndAno(
                pessoa.getId(), mesAnterior.getMesReferencia(), mesAnterior.getAnoReferencia()
        );
        LimiteUso limiteUso = limiteUsoService.findByPessoaId(pessoa.getId());
        var limiteUtilizacao = limiteUso == null ? BigDecimal.ZERO : limiteUso.getLimite();

        var totalAtual = somarValorTotal(resumos);
        var totalAnterior = somarValorTotal(resumosAnteriores);
        var saldo = NumberUtil.roundValue(limiteUtilizacao.subtract(totalAtual));
        var porcentagem = somarPorcentagemDiferenca(totalAtual.doubleValue(), totalAnterior.doubleValue());

        var calculoResponse = new CalculoResumoFaturaUsuarioResponse(
                pessoa.getId(), limiteUtilizacao, porcentagem, saldo, totalAtual, totalAnterior
        );

        return calculoResponse;
    }

    private BigDecimal somarValorDinheiro(MutableList<ResumoFatura> resumos) {
        var dinheiro = resumos.select(re -> re.getFormaPagamento().getId() == 7L)
                .sumOfDouble(resumoFatura -> resumoFatura.getValorAVista().doubleValue());
        return NumberUtil.roundValue(dinheiro);
    }

    private BigDecimal somarValorTotal(MutableList<ResumoFatura> resumos) {
        if (resumos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        var valorTotal = resumos.sumOfDouble(r -> r.getValorTotal().doubleValue());
        return NumberUtil.roundValue(valorTotal);
    }

    private BigDecimal somarValorParcelado(MutableList<ResumoFatura> resumos) {
        var parcelados = resumos.sumOfDouble(r -> r.getValorParcelado().doubleValue());
        return NumberUtil.roundValue(parcelados);
    }

    private BigDecimal somarValorVista(MutableList<ResumoFatura> resumos) {
        var avista = resumos.select(re -> re.getFormaPagamento().getId() != 7L)
                .sumOfDouble(r -> r.getValorAVista().doubleValue());
        return NumberUtil.roundValue(avista);
    }

    private ResumoValorDTO getResumoValorDTO(BigDecimal valor, BigDecimal valorAnterior) {
        var resumoValor = new ResumoValorDTO();
        var porcentagem = somarPorcentagemDiferenca(valor.doubleValue(), valorAnterior.doubleValue());

        resumoValor.setValor(valor);
        resumoValor.setPorcentagem(porcentagem);
        resumoValor.setDiminuiu(porcentagem.intValue() < 0);

        return resumoValor;

    }

    private BigDecimal somarPorcentagemDiferenca(Double valorAtual, Double valorAnterior) {

        Double percentual =  (valorAtual/valorAnterior - 1) * 100;
        return percentual.isNaN() ? BigDecimal.valueOf(0) : NumberUtil.roundValue(percentual);
    }

    @Override
    public MutableList<ResumoFaturaPessoaResponse> calcularDespesas(MesAnoDTO mesAno) {
        var usuario = UsuarioService.usuario();

        if (usuario.isAdmin()) {
            return this.calcularDespesasAdmin(usuario, mesAno);
        } else {
            return this.calcularDespesasUser(usuario, mesAno);
        }
    }

    private MutableList<ResumoFaturaPessoaResponse> calcularDespesasUser(UserCredentialDetails usuario, MesAnoDTO mesAno) {
        var resumos = resumoFaturaRepository.findAllByPessoaIdAndMesAndAno(usuario.getPessoa().getId(), mesAno.getMesReferencia(), mesAno.getAnoReferencia());
        if (resumos.isEmpty()) {
            return FastList.newList();
        }

        MutableList<ResumoFatura> resumosPagamentoPessoa = new FastList<>();
        MutableList<ResumoFatura> resumosAdmin = new FastList<>();
        var pessoa = resumos.getFirst().getPessoa();
        resumos.forEach(res -> {
            if (res.getFormaPagamento().getDono() != null && res.getFormaPagamento().getDono().getId().equals(usuario.getPessoa().getId())) {
                resumosPagamentoPessoa.add(res);
            } else if(res.getFormaPagamento().getId().equals(DINHEIRO) || res.getFormaPagamento().getId().equals(CARNE)) {
                resumosPagamentoPessoa.add(res);
            } else {
                resumosAdmin.add(res);
            }
        });
        var resumoResponse = new ResumoFaturaPessoaResponse();
        MutableList<ValorFormaPagamentoResponse> valoresFormaPagamento = new FastList<>();

        resumoResponse.setId(pessoa.getId());
        resumoResponse.setNome(pessoa.getNome());
        resumoResponse.setNomeCompleto(pessoa.getNomeCompleto());
        resumoResponse.setPerfil(pessoa.getPerfil());

        if (!resumosAdmin.isEmpty()) {
            var valorOutros = new ValorFormaPagamentoResponse();
            valorOutros.setValor( NumberUtil.roundValue( resumosAdmin.sumOfDouble(rr -> rr.getValorTotal().doubleValue()) ));
            valorOutros.setId( (Double.valueOf(Math.random())).longValue());
            valorOutros.setNome(outroNome);
            valorOutros.setType("outros");
            valoresFormaPagamento.add(valorOutros);

        }

        if (!resumosPagamentoPessoa.isEmpty()) {
            resumosPagamentoPessoa.groupBy(ResumoFatura::getFormaPagamento)
                                  .toMap()
                                  .forEach((formaPagamento, res) -> {

                                      var valorFormaPagamento = new ValorFormaPagamentoResponse();

                                      double valor = res.sumOfDouble(rr -> rr.getValorTotal().doubleValue());

                                      var total = NumberUtil.roundValue(valor);

                                      valorFormaPagamento.setId(formaPagamento.getId());
                                      valorFormaPagamento.setValor(total);
                                      valorFormaPagamento.setNome(formaPagamento.getNome());

                                      var isCartao = !Arrays.asList(7L, 6L).contains(formaPagamento.getId());
                                      valorFormaPagamento.setType(isCartao ? "cartao" : formaPagamento.getId() == 7L ? "dinheiro" : "carne");

                                      valoresFormaPagamento.add(valorFormaPagamento);
                                  });
        }

        resumoResponse.setFormasPagamentos(valoresFormaPagamento.sortThisBy(ValorFormaPagamentoResponse::getNome));

        resumoResponse.setSaldo(resumos.getAny().getSaldo());

        resumoResponse.setTotal(
                NumberUtil.roundValue(resumoResponse.getFormasPagamentos().sumOfDouble(r -> r.getValor().doubleValue()))
        );


        return FastList.newListWith(resumoResponse);
    }

    private MutableList<ResumoFaturaPessoaResponse> calcularDespesasAdmin(UserCredentialDetails usuario, MesAnoDTO mesAno) {
        MutableList<ResumoFatura> resumos = resumoFaturaRepository.findAllBydMesAndAno(mesAno.getMesReferencia(), mesAno.getAnoReferencia());

        MutableList<ResumoFaturaPessoaResponse> resumosResponse = new FastList<>();

        MutableMap<Pessoa, RichIterable<ResumoFatura>> resumosPorPessoaAux = resumos.groupBy(ResumoFatura::getPessoa)
                .toMap();
        var resumosPorPessoa = new TreeSortedMap<>(Comparator.comparing(Pessoa::getNome), resumosPorPessoaAux);

        resumosPorPessoa.forEach(((pessoa, resumoFaturas) -> {

            MutableList<ValorFormaPagamentoResponse> valoresFormaPagamento = new FastList<>();

            var porFormaPagamento = resumoFaturas.groupBy(ResumoFatura::getFormaPagamento).toMap();

            var resumoResponse = new ResumoFaturaPessoaResponse();
            resumoResponse.setId(pessoa.getId());
            resumoResponse.setNome(pessoa.getNome());
            resumoResponse.setNomeCompleto(pessoa.getNomeCompleto());
            resumoResponse.setPerfil(pessoa.getPerfil());

            porFormaPagamento.forEach((formaPagamento, res) -> {

                var valorFormaPagamento = new ValorFormaPagamentoResponse();

                double valor = res.sumOfDouble(rr -> rr.getValorTotal().doubleValue());

                var total = NumberUtil.roundValue(valor);

                valorFormaPagamento.setId(formaPagamento.getId());
                valorFormaPagamento.setValor(total);
                valorFormaPagamento.setNome(formaPagamento.getNome());

                var isCartao = !Arrays.asList(7L, 6L).contains(formaPagamento.getId());
                valorFormaPagamento.setType(isCartao ? "cartao" : formaPagamento.getId() == 7L ? "dinheiro" : "carne");

                valoresFormaPagamento.add(valorFormaPagamento);
            });

//            Collections.sort(valoresFormaPagamento);

            resumoResponse.setFormasPagamentos(valoresFormaPagamento.sortThisBy(ValorFormaPagamentoResponse::getNome));

            resumoResponse.setSaldo(resumoFaturas.getAny().getSaldo());

            resumoResponse.setTotal(
                    NumberUtil.roundValue(resumoResponse.getFormasPagamentos().sumOfDouble(r -> r.getValor().doubleValue()))
            );

            resumosResponse.add(resumoResponse);

        }));

        return resumosResponse;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MutableList<ResumoFatura> getFaturasUltimosMeses(MutableList<MesAnoChart> mesesAnos, Pessoa pessoa) {
        String sql = " select rf " +
                "from ResumoFatura rf " +
                "inner join fetch rf.formaPagamento fp " +
                "inner join fetch rf.pessoa p " +
                "inner join fetch rf.mes m " +
                "where 1 = 1 ";
        Map<String, Object> params = new LinkedHashMap<>();

        if (pessoa != null) {
            sql += " and p.id = :pessoaId";
            params.put("pessoaId", pessoa.getId());
        }

        var and = mesesAnos.collectWithIndex((mesAno, index) -> {
            var mesPlaceholder = ":mes" + index;
            var anoPlaceholder = ":ano" + index;
            params.put(mesPlaceholder.replace(":", ""), mesAno.getFechamento());
            params.put(anoPlaceholder.replace(":", ""), mesAno.getAnoFechamento());

            return " ( rf.id.mesId = " + mesPlaceholder + " and rf.id.ano = " + anoPlaceholder + " ) ";
        }).makeString(" or \n");

        sql +=  " and ( " + and + ") ";
        var query = em.createQuery(sql, ResumoFatura.class);

        params.forEach(query::setParameter);

        var valores = query.getResultList();

        return new FastList<>(valores);
    }
}
