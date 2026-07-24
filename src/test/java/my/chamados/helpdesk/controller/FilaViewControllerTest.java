package my.chamados.helpdesk.controller;

import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.repository.UsuarioRepository;
import my.chamados.helpdesk.service.ChamadoService;
import my.chamados.helpdesk.service.CustomUserDetailsService;
import my.chamados.helpdesk.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilaViewController.class)
class FilaViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChamadoService chamadoService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService; // Necessário para o Spring Security

    @Test
    @WithMockUser(username = "admin@helpdesk.com", authorities = {"ADMIN"})
    void quandoAdminAcessaPainel_deveRetornarViewFilaEStatusOk() throws Exception {
        // Arrange
        Usuario admin = new Usuario();
        admin.setEmail("admin@helpdesk.com");
        admin.setPerfil(Usuario.Perfil.ADMIN);
        when(usuarioRepository.findByEmail("admin@helpdesk.com")).thenReturn(Optional.of(admin));

        // Act & Assert
        mockMvc.perform(get("/painel-fila"))
                .andExpect(status().isOk())
                .andExpect(view().name("fila"))
                .andExpect(model().attributeExists("usuarioLogado", "countFilaGeral"));
    }

    @Test
    @WithMockUser(username = "cliente@helpdesk.com", authorities = {"CLIENTE"})
    void quandoClienteAcessaPainel_deveRetornarViewFilaEAtributosDoCliente() throws Exception {
        // Arrange
        Usuario cliente = new Usuario();
        cliente.setEmail("cliente@helpdesk.com");
        cliente.setPerfil(Usuario.Perfil.CLIENTE);
        when(usuarioRepository.findByEmail("cliente@helpdesk.com")).thenReturn(Optional.of(cliente));

        // Act & Assert
        mockMvc.perform(get("/painel-fila"))
                .andExpect(status().isOk())
                .andExpect(view().name("fila"))
                .andExpect(model().attributeExists("meusChamadosAtivos", "countChamadosAtivos"));
    }

    @Test
    void quandoUsuarioNaoAutenticadoAcessaPainel_deveRedirecionarParaLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/painel-fila"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}