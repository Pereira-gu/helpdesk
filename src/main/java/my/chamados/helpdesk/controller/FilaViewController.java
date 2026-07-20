package my.chamados.helpdesk.controller;

import my.chamados.helpdesk.model.Categoria;
import my.chamados.helpdesk.model.Chamado;
import my.chamados.helpdesk.model.Prioridade;
import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.service.ChamadoService;
import my.chamados.helpdesk.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FilaViewController {

    @Autowired
    private ChamadoService chamadoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Carrega a página principal com todos os dados necessários
    @GetMapping("/painel-fila")
    public String exibirFila(Model model) {
        // Listas existentes
        model.addAttribute("chamados", chamadoService.listarFila());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("prioridades", Prioridade.values());
        model.addAttribute("perfis", Usuario.Perfil.values());

        // NOVAS LISTAS: Adiciona os chamados em atendimento e resolvidos ao model
        model.addAttribute("chamadosEmAtendimento", chamadoService.listarChamadosEmAtendimento());
        model.addAttribute("chamadosResolvidos", chamadoService.listarChamadosResolvidos());

        return "fila";
    }

    // Ação: Aceitar/Assumir Chamado
    @PostMapping("/painel-fila/assumir")
    public String assumirChamadoTela(@RequestParam Long chamadoId, @RequestParam Long tecnicoId) {
        try {
            chamadoService.assumirChamado(chamadoId, tecnicoId);
        } catch (Exception e) {
            System.out.println("Erro ao assumir: " + e.getMessage());
        }
        return "redirect:/painel-fila";
    }

    // Ação: Criar Novo Chamado via Formulário
    @PostMapping("/painel-fila/novo-chamado")
    public String criarChamadoTela(@RequestParam String titulo,
                                   @RequestParam String descricao,
                                   @RequestParam Categoria categoria,
                                   @RequestParam Prioridade prioridade,
                                   @RequestParam Long clienteId) {
        try {
            Chamado chamado = new Chamado();
            chamado.setTitulo(titulo);
            chamado.setDescricao(descricao);
            chamado.setCategoria(categoria);
            chamado.setPrioridade(prioridade);

            Usuario cliente = new Usuario();
            cliente.setId(clienteId);
            chamado.setCliente(cliente);

            chamadoService.abrirChamado(chamado);
        } catch (Exception e) {
            System.out.println("Erro ao criar chamado: " + e.getMessage());
        }
        return "redirect:/painel-fila";
    }

    // Ação: Cadastrar Usuário via Formulário
    @PostMapping("/painel-fila/novo-usuario")
    public String cadastrarUsuarioTela(@RequestParam String nome,
                                       @RequestParam String email,
                                       @RequestParam String senha,
                                       @RequestParam Usuario.Perfil perfil) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setPerfil(perfil);

            usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
        return "redirect:/painel-fila";
    }

    @GetMapping("/painel-fila/chamado/{id}")
    public String exibirDetalhesChamado(@PathVariable Long id, Model model) {
        try {
            Chamado chamado = chamadoService.buscarPorId(id);
            
            model.addAttribute("chamado", chamado);
            model.addAttribute("historico", chamadoService.listarHistoricoDoChamado(id));
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "detalhes"; // Nome do novo template HTML
        } catch (Exception e) {
            System.out.println("Erro ao carregar detalhes: " + e.getMessage());
            return "redirect:/painel-fila";
        }
    }

    @PostMapping("/painel-fila/chamado/{id}/comentar")
    public String adicionarComentarioTela(@PathVariable Long id, 
                                          @RequestParam Long usuarioId, 
                                          @RequestParam String texto,
                                          @RequestParam(required = false) boolean resolver,
                                          RedirectAttributes redirectAttributes) {
        try {
            if (resolver) {
                chamadoService.resolverChamado(id, texto);
                redirectAttributes.addFlashAttribute("sucesso", "Chamado resolvido com sucesso!");
            } else {
                chamadoService.adicionarComentario(id, usuarioId, texto);
                redirectAttributes.addFlashAttribute("sucesso", "Comentário adicionado à linha do tempo!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro na operação: " + e.getMessage());
        }
        return "redirect:/painel-fila/chamado/" + id;
    }
}