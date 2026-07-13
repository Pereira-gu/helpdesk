package my.chamados.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "chamados")
@Data
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChamado status = StatusChamado.ABERTO; // Todo chamado nasce ABERTO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataFechamento;

    // Relacionamento: Quem abriu o chamado (Sempre um CLIENTE)
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    // Relacionamento: Quem está atendendo (Pode ser nulo até um TECNICO assumir)
    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    // Executa automaticamente antes de salvar no banco de dados pela primeira vez
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }
}