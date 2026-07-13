package my.chamados.helpdesk.repository;

import my.chamados.helpdesk.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Aqui o Spring já nos dá métodos como save(), findById(), findAll() de graça!
}