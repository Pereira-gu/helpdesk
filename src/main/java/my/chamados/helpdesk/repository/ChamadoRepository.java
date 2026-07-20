package my.chamados.helpdesk.repository;

import my.chamados.helpdesk.model.Chamado;
import my.chamados.helpdesk.model.StatusChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    // A FILA DE ATENDIMENTO: Busca chamados ABERTOS ordenando por Prioridade (Alta -> Baixa) e depois por Data (Mais antigos primeiro)
    @Query("SELECT c FROM Chamado c WHERE c.status = 'ABERTO' ORDER BY " +
            "CASE c.prioridade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 WHEN 'BAIXA' THEN 3 END ASC, " +
            "c.dataCriacao ASC")
    List<Chamado> buscarFilaDeAtendimento();

    // NOVO: Busca chamados que estão sendo atendidos, ordenados pelo mais antigo
    List<Chamado> findByStatusOrderByDataCriacaoAsc(StatusChamado status);

    // NOVO: Busca chamados já resolvidos, ordenados pelo mais recente
    List<Chamado> findByStatusOrderByDataFechamentoDesc(StatusChamado status);
}