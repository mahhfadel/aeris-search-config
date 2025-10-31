package br.com.aeris.aeris_user_config.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pesquisa_colaborador")
public class PesquisaColaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private String token;

    // Relacionamento com USUÁRIO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relacionamento com PESQUISA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pesquisa_id", nullable = false)
    private Pesquisa pesquisa;
}
