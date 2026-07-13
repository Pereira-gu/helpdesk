package my.chamados.helpdesk.repository;

import my.chamados.helpdesk.model.HistoricoChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoricoChamadoRepository extends JpaRepository<HistoricoChamado, Long> {

    // Busca toda a linha do tempo / comentários de um chamado específico ordenado pelo mais antigo ao mais recente
    List<HistoricoChamado> findByChamadoIdOrderByDataRegistroAsc(Long chamadoId);
}