package br.com.aeris.aeris_search_config.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "tipo_pergunta")
public class TipoPergunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @OneToOne
    @JoinColumn(name = "id_pergunta", nullable = false)
    private Pergunta pergunta;

    @OneToMany(mappedBy = "tipoPergunta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Opcoes> opcoes;
}
