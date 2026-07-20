package my.chamados.helpdesk.service;

import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario cadastrarUsuario(Usuario usuario) {
        // Validação básica
        if (usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null) {
            throw new IllegalArgumentException("Nome, email e senha são obrigatórios.");
        }
        // Criptografa a senha antes de salvar
        String senhaCriptografada = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(senhaCriptografada);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (BCrypt.checkpw(senha, usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }
}