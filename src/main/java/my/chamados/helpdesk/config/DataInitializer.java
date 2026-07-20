package my.chamados.helpdesk.config;

import my.chamados.helpdesk.model.Usuario;
import my.chamados.helpdesk.repository.UsuarioRepository;
import my.chamados.helpdesk.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existe um usuário ADMIN
        if (usuarioRepository.findAll().stream().noneMatch(u -> u.getPerfil() == Usuario.Perfil.ADMIN)) {
            System.out.println("Nenhum usuário ADMIN encontrado. Criando usuário padrão...");

            Usuario admin = new Usuario();
            admin.setNome("Administrador Padrão");
            admin.setEmail("admin@helpdesk.com");
            admin.setSenha("admin123"); // A senha será criptografada pelo serviço
            admin.setPerfil(Usuario.Perfil.ADMIN);

            usuarioService.cadastrarUsuario(admin);
            System.out.println("Usuário ADMIN criado com sucesso!");
            System.out.println("Email: admin@helpdesk.com");
            System.out.println("Senha: admin123");
        }
    }
}