package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.Lancamento;
import com.dersaun.apicontas.dao.models.Mes;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.repositories.FormaPagamentoRepository;
import com.dersaun.apicontas.dao.repositories.LancamentoRepository;
import com.dersaun.apicontas.dto.LancamentoDTO;
import com.dersaun.apicontas.dto.Parcelamento;
import com.dersaun.apicontas.dto.PessoasDivisaoDiferenteDTO;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.services.DivisaoService;
import com.dersaun.apicontas.services.LancamentoService;
import com.dersaun.apicontas.services.MesService;
import com.dersaun.apicontas.services.ParcelaService;
import com.dersaun.apicontas.util.NumberUtil;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.dersaun.apicontas.exceptions.StandardHttpException.badRequest;
import static com.dersaun.apicontas.exceptions.StandardHttpException.notFound;

@Service
public class LancamentoServiceImpl extends SimpleServiceImpl<Lancamento, Long> implements LancamentoService {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private ParcelaService parcelaService;

    @Autowired
    private DivisaoService divisaoService;

    @Autowired
    private MesService mesService;

    @Autowired
    private FormaPagamentoRepository formaPagamentoRepository;

    @Override
    public JpaRepository<Lancamento, Long> getRepository() {
        return lancamentoRepository;
    }

    @Override
    public Lancamento getOne(Long aLong) {
        return lancamentoRepository.findById(aLong)
                .orElseThrow(() -> notFound("Compra não encontrada"));
    }

    @Override
    @Transactional(noRollbackFor = StandardHttpException.class)
    public void delete(Long id) {
        Optional<Lancamento> conta = findById(id);
        if (conta.isEmpty()) {
            throw notFound("Conta não encontrada", "Não foi possível excluir esta compra pois não foi encontrada.");
        }
        try {
            delete(conta.get());
        } catch (Exception e) {
            throw badRequest("Ops", "Ocorreu um erro, por gentileza tente novamente mais tarde");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, noRollbackFor = StandardHttpException.class)
    public Lancamento criarLancamento(LancamentoDTO lancamentoDTO) {
        if (lancamentoDTO.isContaFixa()) {
            return null;
        }
        Lancamento lancamento = fromDto(lancamentoDTO);

        if (lancamentoDTO.isDividido()) {
            lancamento = divisaoService.dividir(lancamentoDTO, lancamento);
        }

        save(lancamento);

        if (lancamento.isParcelado()) {
            var lancamentosParcelamentos = lancamento.getLancamentos().collect(con -> new Parcelamento(con, con.getMes()));
            lancamentosParcelamentos.add(new Parcelamento(lancamento, lancamento.getMes()));
            parcelaService.parcelar(lancamentosParcelamentos);
        }
        return lancamento;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class, noRollbackFor = StandardHttpException.class)
    public void atualizar(Long id, LancamentoDTO lancamentoDTO) {
        var lancamento = getOne(id);
        if (lancamento.isReadOnly()) {
            throw StandardHttpException.badRequest("Conta indisponível para atualização, verifique com a pessoa que realizou o cadastro");
        }

        Boolean sucesso = false;
        var mesAtual = mesService.getMesAtual();

        if (lancamentoDTO.isDiferente()) {
            sucesso = atualizarDiferente(lancamento, lancamentoDTO, lancamentoDTO.getParcelado(), mesAtual);
        } else if (lancamentoDTO.isIgual()) {
            sucesso = atualizarIgualmente(lancamento, lancamentoDTO, lancamentoDTO.getParcelado(), mesAtual)   ;
        } else {
            removerContasDivididas(lancamento, false);
            sucesso = atualizarContaNormal(lancamento, lancamentoDTO, lancamentoDTO.getParcelado(), mesAtual);
        }
    }

    @Override
    public Boolean atualizarDiferente(Lancamento lancamento, LancamentoDTO lancamentoDto, Boolean parcelado, Mes mesAtual) {

        var mudouDivisao = isOutraDivisao(lancamento, lancamentoDto);

        var eraParcelado = lancamento.getParcelado();
        var quantidadeParcelasOriginal = eraParcelado ? lancamento.getQuantidadeParcelas() : 0;
        var redividir = osDevedoresMudaram(lancamento, lancamentoDto) || lancamento.getLancamentos().isEmpty() || mudouDivisao;

        newValuesToConta(lancamento, lancamentoDto);

        if (redividir) {
            removerContasDivididas(lancamento, false);
            lancamento.getLancamentos().clear();
            lancamento = divisaoService.dividirDiferente(lancamentoDto, lancamento);
        } else {
            divisaoService.checkValorTotal(lancamento, lancamentoDto);
            Lancamento finalConta = lancamento;
            lancamento.getLancamentos().each(relacionado -> {

                var pessoaList = lancamentoDto.getPessoasDivididasDiferente()
                                                                                .select(p -> p.getPessoaId() == relacionado.getPessoa().getId());

                var pessoa = pessoaList.get(0);
                updateRelacionado(relacionado, finalConta, pessoa.getValor());
            });
            lancamento.setValorDividido(lancamento.getValor().subtract(lancamentoDto.getTotalPessoasDivididoDiferente()) );
        }

        var salvo = save(lancamento);

        if (salvo != null) {
            parcelaService.atualizar(lancamento, eraParcelado, parcelado, quantidadeParcelasOriginal, redividir, mesAtual);
        }
        return true;
    }

    @Override
    public Boolean atualizarContaNormal(Lancamento lancamento, LancamentoDTO lancamentoDto, Boolean parcelado, Mes mesAtual) {
        Boolean eraParcelado = lancamento.getParcelado();
        var quantidadeParcelasOriginal = lancamento.getQuantidadeParcelas();
        newValuesToConta(lancamento, lancamentoDto);
        lancamento.getLancamentos().clear();
        parcelaService.atualizar(lancamento, eraParcelado, parcelado, quantidadeParcelasOriginal, mesAtual);
        save(lancamento);
        return true;
    }

    @Override
    public Boolean atualizarIgualmente(Lancamento lancamento, LancamentoDTO lancamentoDto, Boolean parcelado, Mes mesAtual) {
        Boolean mudouDivisao = isOutraDivisao(lancamento, lancamentoDto);

        var eraParcelado = lancamento.getParcelado();
        var quantidadeParcelasOriginal = eraParcelado ? lancamento.getQuantidadeParcelas() : 0;

        newValuesToConta(lancamento, lancamentoDto);

        var redividir = osDevedoresMudaram(lancamento, lancamentoDto) || lancamento.getLancamentos().isEmpty() || mudouDivisao;

        if (redividir) {
            removerContasDivididas(lancamento, false);
            lancamento.getLancamentos().clear();
            lancamento = divisaoService.dividirIgualmente(lancamentoDto, lancamento);
        } else {
            var valorDividido = (lancamento.getValor().doubleValue() / (lancamentoDto.getPessoasDivididasIgualmente().size() + 1));
            lancamento.setValorDividido(NumberUtil.roundValue(valorDividido));
            Lancamento finalConta = lancamento;
            lancamento.getLancamentos().each(relacionado -> updateRelacionado(relacionado, finalConta, finalConta.getValorDividido() ));
        }

        var salvo = save(lancamento);

        if (salvo != null) {
            parcelaService.atualizar(salvo, eraParcelado, parcelado, quantidadeParcelasOriginal, redividir, mesAtual);
        }
        return true;
    }

    @Override
    public void removerContasDivididas(Lancamento lancamento, Boolean removerTudo) {
        if (removerTudo) {
            List<Lancamento> toDelete = new ArrayList(Arrays.asList(lancamento.getLancamentos()));
            toDelete.add(lancamento);
            deleteAll(toDelete);
        } else {
            lancamento.setDivisaoId(null);
            lancamento.setValorDividido(null);
            lancamentoRepository.deleteByLancamentoRelacionado(lancamento);
        }
    }

    private Lancamento fromDto(LancamentoDTO lancamentoDTO) {
        var lancamento = new Lancamento();
        var formaPagamento = formaPagamentoRepository.findById(lancamentoDTO.getFormaPagamentoId()).get();
        lancamento.setNome(lancamentoDTO.getNome());
        lancamento.setDescricao(lancamentoDTO.getDescricao());
        lancamento.setValor(lancamentoDTO.getValor());
        lancamento.setDataCompra(lancamentoDTO.getDataCompra());
        lancamento.setAno(lancamento.getDataCompra().getYear());
        lancamento.setMes(mesService.getFromDataCompraAndFormaPagamento(lancamento.getDataCompra(), formaPagamento));
        lancamento.setPago(false);
        lancamento.setTipoConta(
                lancamentoDTO.getFormaPagamentoId() == null ||
                        lancamentoDTO.getFormaPagamentoId() == 0L ||
                        lancamentoDTO.getFormaPagamentoId() == 7L ? 2 : 1);
        lancamento.setParcelado(lancamentoDTO.getParcelado() != null ? lancamentoDTO.getParcelado() : false);
        lancamento.setQuantidadeParcelas(lancamento.getParcelado() ? lancamentoDTO.getQuantidadeParcelas() : 0);
        lancamento.setFormaPagamento(formaPagamento);

        if (lancamentoDTO.isDiferente()) {
            lancamento.setDivisaoId(2);
        } else if (lancamentoDTO.isIgual()) {
            lancamento.setDivisaoId(1);
        }
        lancamento.setPessoa(new Pessoa(lancamentoDTO.getPessoaId()));
        return lancamento;
    }

    private boolean isOutraDivisao(Lancamento lancamento, LancamentoDTO lancamentoDTO) {
        boolean mudouDivisao = false;

        if ((lancamentoDTO.isIgual() || lancamentoDTO.isDiferente()) && lancamento.getDivisaoId() == null) {
            mudouDivisao = true;
        } else if (lancamento.getDivisaoId() == 1 && lancamentoDTO.isDiferente()) {
            mudouDivisao = true;
        } else if ( lancamento.getDivisaoId() == 2 && lancamentoDTO.isIgual()) {
            mudouDivisao = true;
        }
        return mudouDivisao;
    }

    private boolean osDevedoresMudaram(Lancamento lancamento, LancamentoDTO lancamentoDTO) {
        MutableList<Long> pessoasAtuais = FastList.newListWith(lancamento.getPessoa().getId());
        lancamento.getLancamentos().each(lan -> {
            pessoasAtuais.add(lan.getPessoa().getId());
        });


        if (lancamentoDTO.isIgual() && lancamento.getDivisaoId() == 1) {
            MutableList<Long> novosIgual = FastList.newListWith(lancamentoDTO.getPessoaId());

            if (pessoasAtuais.size() != novosIgual.size() ){
                return true;
            }
            novosIgual.removeIf(pessoasAtuais::contains);
            return novosIgual.size() > 0;

        } else if (lancamentoDTO.isDiferente() && lancamento.getDivisaoId() == 2) {

            var novos = lancamentoDTO.getPessoasDivididasDiferente()
                                                      .collect(PessoasDivisaoDiferenteDTO::getPessoaId);

            if (pessoasAtuais.size() != novos.size() ){
                return true;
            }
            novos.removeIf(pessoasAtuais::contains);

            return novos.size() > 0;
        }
        return false;

    }

    private void updateRelacionado(Lancamento relacionado, Lancamento lancamento, BigDecimal valorDividido) {
        relacionado.setNome(lancamento.getNome());
        relacionado.setDescricao(lancamento.getDescricao());
        relacionado.setValor(lancamento.getValor());
        relacionado.setFormaPagamento(lancamento.getFormaPagamento());
        relacionado.setParcelado(lancamento.getParcelado());
        relacionado.setQuantidadeParcelas(lancamento.getQuantidadeParcelas());
        relacionado.setTipoConta(lancamento.getTipoConta());
        relacionado.setMes(lancamento.getMes());
        relacionado.setDivisaoId(lancamento.getDivisaoId());
        relacionado.setValorDividido(valorDividido);
    }

    /**
     * Reassign basic new values to conta
     * Rever valor dividido caso seja dividido
     * Parcelas etc.
     * @return
     */
    private void newValuesToConta(Lancamento lancamento, LancamentoDTO lancamentoDTO) {

        lancamento.setNome(lancamentoDTO.getNome());
        lancamento.setDescricao(lancamentoDTO.getDescricao());
        lancamento.setValor(lancamentoDTO.getValor());
        lancamento.setParcelado(lancamentoDTO.getParcelado());
        lancamento.setQuantidadeParcelas(lancamentoDTO.getQuantidadeParcelas());

        if (lancamento.getPessoa().getId() != lancamentoDTO.getPessoaId()) {
            lancamento.setPessoa(new Pessoa(lancamentoDTO.getPessoaId()));
        }

        if (lancamento.getFormaPagamento().getId()!= lancamentoDTO.getFormaPagamentoId() ) {
            lancamento.setFormaPagamento(formaPagamentoRepository.getById(lancamentoDTO.getFormaPagamentoId()));
        }
        if (!lancamento.getDataCompra().equals(lancamentoDTO.getDataCompra()) ) {
            lancamento.setDataCompra(lancamentoDTO.getDataCompra());
            lancamento.setMes(mesService.getFromDataCompraAndFormaPagamento(lancamento.getDataCompra(), lancamento.getFormaPagamento()));
        }
        lancamento.setTipoConta(lancamento.getFormaPagamento() == null  || lancamento.getFormaPagamento().getId() == 7 ? 2 : 1);

        if (lancamentoDTO.isDiferente()) {
            lancamento.setDivisaoId(2);
        } else if (lancamentoDTO.isIgual()) {
            lancamento.setDivisaoId(1);
        } else {
            lancamento.setDivisaoId(null);
        }
        lancamento.setValorDividido(null);
    }
}
