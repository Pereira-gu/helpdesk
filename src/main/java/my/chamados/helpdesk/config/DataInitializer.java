package my.chamados.helpdesk.config;

import my.chamados.helpdesk.model.*;
import my.chamados.helpdesk.repository.ChamadoRepository;
import my.chamados.helpdesk.repository.HistoricoChamadoRepository;
import my.chamados.helpdesk.repository.UsuarioRepository;
import my.chamados.helpdesk.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private HistoricoChamadoRepository historicoChamadoRepository;

    @Override
    public void run(String... args) throws Exception {
        Usuario admin = criarUsuarioSeNaoExistir("Administrador Padrão", "admin@helpdesk.com", "admin123", Usuario.Perfil.ADMIN);
        Usuario tecnico1 = criarUsuarioSeNaoExistir("Carlos Técnico", "tecnico@helpdesk.com", "123456", Usuario.Perfil.TECNICO);
        Usuario tecnico2 = criarUsuarioSeNaoExistir("Mariana Suporte", "mariana@helpdesk.com", "123456", Usuario.Perfil.TECNICO);
        Usuario cliente1 = criarUsuarioSeNaoExistir("Juliana Silva", "juliana@helpdesk.com", "123456", Usuario.Perfil.CLIENTE);
        Usuario cliente2 = criarUsuarioSeNaoExistir("Roberto Alves", "roberto@helpdesk.com", "123456", Usuario.Perfil.CLIENTE);

        if (chamadoRepository.count() == 0) {
            // ABERTOS
            criarChamado("Impressora não responde na rede", "A impressora do setor financeiro parou de imprimir repentinamente.", Categoria.IMPRESSORA, Prioridade.ALTA, StatusChamado.ABERTO, cliente1, null, null);
            criarChamado("Esqueci minha senha do ERP", "Preciso de reset da minha senha do sistema principal de vendas.", Categoria.SENHA_E_ACESSO, Prioridade.MEDIA, StatusChamado.ABERTO, cliente2, null, null);

            // EM ATENDIMENTO
            Chamado cEmAndamento = criarChamado("Lentidão no sistema de estoque", "Ao consultar itens, a tela fica carregando por mais de 2 minutos.", Categoria.SISTEMA, Prioridade.ALTA, StatusChamado.EM_ATENDIMENTO, cliente1, tecnico1, null);
            gerarHistorico(cEmAndamento, tecnico1, "O técnico Carlos assumiu o atendimento deste chamado.");
            gerarHistorico(cEmAndamento, cliente1, "Obrigado Carlos! O problema ocorre principalmente no módulo de buscas.");

            // RESOLVIDOS
            Chamado cResolvido1 = criarChamado("Sem conexão com a internet", "O cabo de rede desconectou e o computador perdeu acesso.", Categoria.REDE_E_INTERNET, Prioridade.ALTA, StatusChamado.RESOLVIDO, cliente2, tecnico1, LocalDateTime.now().minusDays(1));
            gerarHistorico(cResolvido1, tecnico1, "O técnico Carlos assumiu o atendimento deste chamado.");
            gerarHistorico(cResolvido1, tecnico1, "CHAMADO RESOLVIDO. Solução aplicada: Substituído o cabo de rede defeituoso e reconfigurado o IP estático.");

            Chamado cResolvido2 = criarChamado("Instalação de ramal telefônico", "Solicitação de novo ramal para a mesa do estagiário.", Categoria.TELEFONIA, Prioridade.BAIXA, StatusChamado.RESOLVIDO, cliente1, tecnico2, LocalDateTime.now().minusDays(3));
            gerarHistorico(cResolvido2, tecnico2, "A técnica Mariana assumiu o atendimento deste chamado.");
            gerarHistorico(cResolvido2, tecnico2, "CHAMADO RESOLVIDO. Solução aplicada: Criado ramal 4022 no PABX virtual e configurado aparelho VoIP.");
        }
    }

    private Usuario criarUsuarioSeNaoExistir(String nome, String email, String senha, Usuario.Perfil perfil) {
        return usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            u.setSenha(senha);
            u.setPerfil(perfil);
            return usuarioService.cadastrarUsuario(u);
        });
    }

    private Chamado criarChamado(String titulo, String descricao, Categoria categoria, Prioridade prioridade, StatusChamado status, Usuario cliente, Usuario tecnico, LocalDateTime dataFechamento) {
        Chamado c = new Chamado();
        c.setTitulo(titulo);
        c.setDescricao(descricao);
        c.setCategoria(categoria);
        c.setPrioridade(prioridade);
        c.setStatus(status);
        c.setCliente(cliente);
        c.setTecnico(tecnico);
        c.setDataFechamento(dataFechamento);
        return chamadoRepository.save(c);
    }

    private void gerarHistorico(Chamado chamado, Usuario usuario, String texto) {
        HistoricoChamado h = new HistoricoChamado();
        h.setChamado(chamado);
        h.setUsuario(usuario);
        h.setDescricao(texto);
        historicoChamadoRepository.save(h);
    }
}