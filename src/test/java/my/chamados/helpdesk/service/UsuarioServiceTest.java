package my.chamados.helpdesk.service;

import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void quandoCadastrarUsuario_deveCriptografarSenhaESalvar() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senha123");

        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario usuarioSalvo = usuarioService.cadastrarUsuario(usuario);

        // Assert
        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getSenha()).isEqualTo("senhaCriptografada");
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void quandoListarTodos_deveRetornarListaDeUsuarios() {
        // Arrange
        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2));

        // Act
        List<Usuario> usuarios = usuarioService.listarTodos();

        // Assert
        assertThat(usuarios).hasSize(2);
        verify(usuarioRepository, times(1)).findAll();
    }
}