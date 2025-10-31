package br.com.aeris.aeris_user_config.model;

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


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_pergunta_id", nullable = false)
    private TipoPergunta tipoPergunta;
}
