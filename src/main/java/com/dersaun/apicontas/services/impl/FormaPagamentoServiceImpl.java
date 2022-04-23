package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.FormaPagamento;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.repositories.FormaPagamentoRepository;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.payload.request.AtualizarFormaPagamentoRequestDto;
import com.dersaun.apicontas.payload.request.CriarFormaPagamentoRequestDTO;
import com.dersaun.apicontas.payload.response.ListarTodosFormaPagamentoResponseDTO;
import com.dersaun.apicontas.services.FormaPagamentoService;
import com.dersaun.apicontas.services.UsuarioService;
import org.eclipse.collections.api.list.MutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static com.dersaun.apicontas.services.impl.ResumoFaturaServiceImpl.CARNE;
import static com.dersaun.apicontas.services.impl.ResumoFaturaServiceImpl.DINHEIRO;

@Service
public class FormaPagamentoServiceImpl implements FormaPagamentoService {

    @Autowired
    private FormaPagamentoRepository formaPagamentoRepository;

    @Override
    public FormaPagamento findOne(Long id) {
        return formaPagamentoRepository.findById(id)
                .orElseThrow(() -> StandardHttpException.notFound("Forma de pagamento não encontrado"));
    }

    @Override
    public MutableList<FormaPagamento> findAllByPermissao() {
        var usuario = UsuarioService.usuario();
        if (usuario.isAdmin()) {
            return formaPagamentoRepository.findAllByAtivoTrueOrderByNomeAsc();
        } else {
            return formaPagamentoRepository.findAllByAtivoTrueAndDonoOrIdInOrderByNomeAsc(usuario.getPessoa(), Arrays.asList(DINHEIRO, CARNE));

        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, noRollbackFor = StandardHttpException.class)
    public FormaPagamento create(CriarFormaPagamentoRequestDTO dto) {
        var formaPagamento = new FormaPagamento();
        var pessoa = new Pessoa(dto.getPessoaId());
        formaPagamento.setDono(pessoa);
        formaPagamento.setNome(dto.getNome());
        formaPagamento.setAtivo(true);
        formaPagamento.setVencimento(dto.getDiaVencimento());
        formaPagamento.setCor(dto.getCor());

        return formaPagamentoRepository.save(formaPagamento);
    }

    @Override
    public ListarTodosFormaPagamentoResponseDTO getAll() {
        MutableList<FormaPagamento> formasPagamentos = null;
        var usuario = UsuarioService.usuario();

        if (usuario.isAdmin()) {
            formasPagamentos = formaPagamentoRepository.findAllByOrderByNomeAsc();
        } else {
            formasPagamentos = formaPagamentoRepository.findAllByDonoOrIdInOrderByNomeAsc(usuario.getPessoa(), Arrays.asList(DINHEIRO, CARNE));
        }
        var ativos = formasPagamentos.select(forma -> forma.isAtivo());
        var cancelados = formasPagamentos.select(forma -> !forma.isAtivo());
        var formasPagamentoResponse = new ListarTodosFormaPagamentoResponseDTO(ativos, cancelados);

        return formasPagamentoResponse;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, noRollbackFor = StandardHttpException.class)
    public void atualizarStatus(Long id, Boolean ativo) {
        var formaPagamento = findOne(id);
        formaPagamento.setAtivo(ativo);
        formaPagamentoRepository.save(formaPagamento);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, noRollbackFor = StandardHttpException.class)
    public void atualizar(Long id, AtualizarFormaPagamentoRequestDto dto) {
        var formaPagamento = findOne(id);
        formaPagamento.setNome(dto.getNome());
        formaPagamento.setCor(dto.getCor());
        formaPagamento.setVencimento(dto.getDiaVencimento());
        formaPagamento.setDono(new Pessoa(dto.getPessoaId()));
        formaPagamentoRepository.save(formaPagamento);
    }
}
