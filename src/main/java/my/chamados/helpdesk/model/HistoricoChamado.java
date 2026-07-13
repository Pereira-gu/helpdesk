package my.chamados.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "historicos_chamado")
@Data
public class HistoricoChamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento: O histórico pertence a um Chamado específico
    @ManyToOne
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;

    // Relacionamento: Quem escreveu essa mensagem (pode ser o técnico ou o cliente)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao; // O texto do comentário ou a descrição da solução técnica

    private LocalDateTime dataRegistro;

    @PrePersist
    protected void onCreate() {
        this.dataRegistro = LocalDateTime.now();
    }
}