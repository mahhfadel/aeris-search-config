package br.com.aeris.aeris_user_config.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_pergunta")
public class TipoPergunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "pergunta_id", nullable = false)
    private Pergunta pergunta;
}
