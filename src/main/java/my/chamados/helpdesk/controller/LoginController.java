package my.chamados.helpdesk.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email, @RequestParam String senha, HttpServletRequest request, Model model) {
        Optional<Usuario> usuarioAutenticado = usuarioService.autenticar(email, senha);

        if (usuarioAutenticado.isPresent()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioLogado", usuarioAutenticado.get());
            return "redirect:/painel-fila";
        } else {
            model.addAttribute("erro", "Credenciais inválidas. Tente novamente.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}