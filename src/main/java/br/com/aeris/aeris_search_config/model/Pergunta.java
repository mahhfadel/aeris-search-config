package br.com.aeris.aeris_user_config.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pergunta")
public class Pergunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criado_em;

    @Column(nullable = false)
    private String pergunta;

    @Column(nullable = false)
    private String adjetivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pesquisa_id", nullable = false)
    private Pesquisa pesquisa;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_pergunta_id", nullable = false)
    private TipoPergunta tipoPergunta;
}
