package my.chamados.helpdesk.service;

import my.chamados.helpdesk.model.Chamado;
import my.chamados.helpdesk.model.StatusChamado;
import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.repository.ChamadoRepository;
import my.chamados.helpdesk.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import my.chamados.helpdesk.model.HistoricoChamado;
import my.chamados.helpdesk.repository.HistoricoChamadoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChamadoService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistoricoChamadoRepository historicoChamadoRepository;

    // Regra: Criar chamado padrão (Sempre nasce ABERTO)
    public Chamado abrirChamado(Chamado chamado) {
        // Valida se o cliente informado existe e realmente é um CLIENTE
        Usuario cliente = usuarioRepository.findById(chamado.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        if (cliente.getPerfil() != Usuario.Perfil.CLIENTE) {
            throw new RuntimeException("Apenas usuários com perfil CLIENTE podem abrir chamados.");
        }

        chamado.setCliente(cliente);
        chamado.setStatus(StatusChamado.ABERTO);
        return chamadoRepository.save(chamado);
    }

    // Técnico assume o chamado
    @Transactional
    public Chamado assumirChamado(Long chamadoId, Long tecnicoId) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado."));

        Usuario tecnico = usuarioRepository.findById(tecnicoId)
                .orElseThrow(() -> new RuntimeException("Técnico não encontrado."));

        if (tecnico.getPerfil() != Usuario.Perfil.TECNICO) {
            throw new RuntimeException("Apenas usuários com perfil TECNICO podem assumir chamados.");
        }

        if (chamado.getStatus() != StatusChamado.ABERTO) {
            throw new RuntimeException("Este chamado não está mais disponível na fila.");
        }

        chamado.setTecnico(tecnico);
        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);
        chamadoRepository.save(chamado);

        // REGRA DE HISTÓRICO: Grava no histórico que o técnico assumiu
        HistoricoChamado historico = new HistoricoChamado();
        historico.setChamado(chamado);
        historico.setUsuario(tecnico);
        historico.setDescricao("O técnico " + tecnico.getNome() + " assumiu o atendimento deste chamado.");
        historicoChamadoRepository.save(historico);

        return chamado;
    }

    // Regra: Finalizar/Resolver o chamado (Agora exige a descrição da solução)
    @Transactional
    public Chamado resolverChamado(Long chamadoId, String solucaoTecnica) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado."));

        if (chamado.getStatus() != StatusChamado.EM_ATENDIMENTO) {
            throw new RuntimeException("O chamado precisa estar em atendimento para ser resolvido.");
        }

        if (solucaoTecnica == null || solucaoTecnica.trim().isEmpty()) {
            throw new RuntimeException("É obrigatório informar a solução técnica para encerrar o chamado.");
        }

        chamado.setStatus(StatusChamado.RESOLVIDO);
        chamado.setDataFechamento(LocalDateTime.now());
        chamadoRepository.save(chamado);

        // REGRA DE HISTÓRICO: Grava a solução final dada pelo técnico
        HistoricoChamado historico = new HistoricoChamado();
        historico.setChamado(chamado);
        historico.setUsuario(chamado.getTecnico()); // O técnico que atendeu
        historico.setDescricao("CHAMADO RESOLVIDO. Solução aplicada: " + solucaoTecnica);
        historicoChamadoRepository.save(historico);

        return chamado;
    }

    // Regra: Adicionar um comentário avulso na linha do tempo do chamado
    @Transactional
    public HistoricoChamado adicionarComentario(Long chamadoId, Long usuarioId, String textoComentario) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado."));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        HistoricoChamado historico = new HistoricoChamado();
        historico.setChamado(chamado);
        historico.setUsuario(usuario);
        historico.setDescricao(textoComentario);

        return historicoChamadoRepository.save(historico);
    }

    // Listar a linha do tempo de um chamado
    public List<HistoricoChamado> listarHistoricoDoChamado(Long chamadoId) {
        return historicoChamadoRepository.findByChamadoIdOrderByDataRegistroAsc(chamadoId);
    }

    public List<Chamado> listarFila() {
        return chamadoRepository.buscarFilaDeAtendimento();
    }
}