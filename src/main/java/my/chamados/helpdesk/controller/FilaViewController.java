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
        model.addAttribute("chamados", chamadoService.listarFila());
        model.addAttribute("usuarios", usuarioRepository.findAll()); // Lista usuários para vincular ao chamado
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("prioridades", Prioridade.values());
        model.addAttribute("perfis", Usuario.Perfil.values());
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
}