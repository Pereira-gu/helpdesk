package my.chamados.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data // O Lombok gera getters, setters, equals e hashcode automaticamente
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil; // CLIENTE ou TECNICO

    public enum Perfil {
        CLIENTE,
        TECNICO
    }
}