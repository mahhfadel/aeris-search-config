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


    @ManyToOne
    @JoinColumn(name = "tipo_pergunta_id")
    private TipoPergunta tipoPergunta;
}
