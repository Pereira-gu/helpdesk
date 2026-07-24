package my.chamados.helpdesk.service;

import my.chamados.helpdesk.model.Chamado;
import my.chamados.helpdesk.model.StatusChamado;
import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.repository.ChamadoRepository;
import my.chamados.helpdesk.repository.HistoricoChamadoRepository;
import my.chamados.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HistoricoChamadoRepository historicoChamadoRepository;

    @InjectMocks
    private ChamadoService chamadoService;

    @Test
    void quandoAbrirChamado_deveDefinirStatusAbertoESalvar() {
        // Arrange
        Usuario cliente = new Usuario();
        cliente.setId(1L);
        cliente.setPerfil(Usuario.Perfil.CLIENTE);

        Chamado chamado = new Chamado();
        chamado.setCliente(cliente);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(chamadoRepository.save(any(Chamado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Chamado chamadoAberto = chamadoService.abrirChamado(chamado);

        // Assert
        assertThat(chamadoAberto.getStatus()).isEqualTo(StatusChamado.ABERTO);
        verify(chamadoRepository, times(1)).save(chamado);
    }

    @Test
    void quandoAssumirChamado_deveMudarStatusParaEmAtendimento() {
        // Arrange
        Usuario tecnico = new Usuario();
        tecnico.setId(1L);
        tecnico.setPerfil(Usuario.Perfil.TECNICO);

        Chamado chamado = new Chamado();
        chamado.setStatus(StatusChamado.ABERTO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(chamadoRepository.findById(anyLong())).thenReturn(Optional.of(chamado));

        // Act
        chamadoService.assumirChamado(1L, 1L);

        // Assert
        assertThat(chamado.getStatus()).isEqualTo(StatusChamado.EM_ATENDIMENTO);
        assertThat(chamado.getTecnico()).isEqualTo(tecnico);
        verify(chamadoRepository, times(1)).save(chamado);
        verify(historicoChamadoRepository, times(1)).save(any());
    }

    @Test
    void quandoResolverChamado_deveMudarStatusParaResolvido() {
        // Arrange
        Chamado chamado = new Chamado();
        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);

        when(chamadoRepository.findById(anyLong())).thenReturn(Optional.of(chamado));

        // Act
        chamadoService.resolverChamado(1L, "Solução de teste");

        // Assert
        assertThat(chamado.getStatus()).isEqualTo(StatusChamado.RESOLVIDO);
        assertThat(chamado.getDataFechamento()).isNotNull();
        verify(chamadoRepository, times(1)).save(chamado);
        verify(historicoChamadoRepository, times(1)).save(any());
    }

    @Test
    void quandoBuscarPorIdInexistente_deveLancarExcecao() {
        // Arrange
        when(chamadoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> chamadoService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Chamado não encontrado");
    }
}