package my.chamados.helpdesk.controller;

import jakarta.servlet.http.HttpSession;
import my.chamados.helpdesk.model.Categoria;
import my.chamados.helpdesk.model.Chamado;
import my.chamados.helpdesk.model.Prioridade;
import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.service.ChamadoService;
import my.chamados.helpdesk.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@SessionAttributes("usuarioLogado")
public class FilaViewController {

    @Autowired
    private ChamadoService chamadoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/painel-fila")
    public String exibirFila(Model model, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarioLogado", usuarioLogado);

        switch (usuarioLogado.getPerfil()) {
            case CLIENTE:
                List<Chamado> meusChamadosAtivos = chamadoService.listarChamadosAtivosDoCliente(usuarioLogado.getId());
                List<Chamado> meusChamadosResolvidos = chamadoService.listarChamadosResolvidosDoCliente(usuarioLogado.getId());
                model.addAttribute("meusChamadosAtivos", meusChamadosAtivos);
                model.addAttribute("chamadosResolvidos", meusChamadosResolvidos);
                model.addAttribute("countChamadosAtivos", meusChamadosAtivos.size());
                model.addAttribute("countChamadosResolvidos", meusChamadosResolvidos.size());
                break;

            case ADMIN:
                model.addAttribute("usuariosLista", usuarioService.listarTodos());
                List<Chamado> chamadosFilaAdmin = chamadoService.listarFila();
                List<Chamado> chamadosEmAtendimentoAdmin = chamadoService.listarChamadosEmAtendimento();
                List<Chamado> chamadosResolvidosAdmin = chamadoService.listarChamadosResolvidos();
                model.addAttribute("countFilaGeral", chamadosFilaAdmin.size());
                model.addAttribute("countEmAtendimento", chamadosEmAtendimentoAdmin.size());
                model.addAttribute("countTotalResolvidos", chamadosResolvidosAdmin.size());

            case TECNICO:
                model.addAttribute("chamados", chamadoService.listarFila());
                model.addAttribute("chamadosEmAtendimento", chamadoService.listarChamadosEmAtendimento());
                model.addAttribute("chamadosResolvidos", chamadoService.listarChamadosResolvidos());
                break;
        }

        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("prioridades", Prioridade.values());
        model.addAttribute("perfis", Usuario.Perfil.values());
        return "fila";
    }

    @PostMapping("/painel-fila/assumir")
    public String assumirChamadoTela(@RequestParam Long chamadoId, @ModelAttribute("usuarioLogado") Usuario tecnico) {
        try {
            chamadoService.assumirChamado(chamadoId, tecnico.getId());
        } catch (Exception e) {
            System.out.println("Erro ao assumir: " + e.getMessage());
        }
        return "redirect:/painel-fila";
    }

    @PostMapping("/painel-fila/novo-chamado")
    public String criarChamadoTela(@RequestParam String titulo,
                                   @RequestParam String descricao,
                                   @RequestParam Categoria categoria,
                                   @RequestParam Prioridade prioridade,
                                   @ModelAttribute("usuarioLogado") Usuario cliente) {
        try {
            Chamado chamado = new Chamado();
            chamado.setTitulo(titulo);
            chamado.setDescricao(descricao);
            chamado.setCategoria(categoria);
            chamado.setPrioridade(prioridade);
            chamado.setCliente(cliente);
            chamadoService.abrirChamado(chamado);
        } catch (Exception e) {
            System.out.println("Erro ao criar chamado: " + e.getMessage());
        }
        return "redirect:/painel-fila";
    }

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
            usuarioService.cadastrarUsuario(usuario);
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
        return "redirect:/painel-fila";
    }

    @PostMapping("/painel-fila/excluir-usuario")
    public String excluirUsuario(@RequestParam Long usuarioId, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado != null && usuarioLogado.getPerfil() == Usuario.Perfil.ADMIN) {
            if (!usuarioLogado.getId().equals(usuarioId)) {
                try {
                    usuarioService.deletarUsuario(usuarioId);
                } catch (Exception e) {
                    System.out.println("Erro ao excluir usuário: " + e.getMessage());
                }
            }
        }
        return "redirect:/painel-fila";
    }

    @GetMapping("/painel-fila/chamado/{id}")
    public String exibirDetalhesChamado(@PathVariable Long id, Model model) {
        try {
            Chamado chamado = chamadoService.buscarPorId(id);
            model.addAttribute("chamado", chamado);
            model.addAttribute("historico", chamadoService.listarHistoricoDoChamado(id));
            return "detalhes";
        } catch (Exception e) {
            System.out.println("Erro ao carregar detalhes: " + e.getMessage());
            return "redirect:/painel-fila";
        }
    }

    @PostMapping("/painel-fila/chamado/{id}/comentar")
    public String adicionarComentarioTela(@PathVariable Long id,
                                          @RequestParam String texto,
                                          @RequestParam(required = false) boolean resolver,
                                          @ModelAttribute("usuarioLogado") Usuario usuario,
                                          RedirectAttributes redirectAttributes) {
        try {
            if (resolver) {
                chamadoService.resolverChamado(id, texto);
                redirectAttributes.addFlashAttribute("sucesso", "Chamado resolvido com sucesso!");
            } else {
                chamadoService.adicionarComentario(id, usuario.getId(), texto);
                redirectAttributes.addFlashAttribute("sucesso", "Comentário adicionado à linha do tempo!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro na operação: " + e.getMessage());
        }
        return "redirect:/painel-fila/chamado/" + id;
    }
}