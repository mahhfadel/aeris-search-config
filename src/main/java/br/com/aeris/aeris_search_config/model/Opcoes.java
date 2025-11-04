package br.com.aeris.aeris_search_config.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "opcoes")
public class Opcoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipoPerguntaId", nullable = false)
    private TipoPergunta tipoPergunta;
}
