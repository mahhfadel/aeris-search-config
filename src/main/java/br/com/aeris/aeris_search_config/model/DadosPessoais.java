package br.com.aeris.aeris_search_config.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "dados_pessoais")
public class DadosPessoais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private LocalDate contratado_em;

    @Column(nullable = false)
    private LocalDate data_nascimento;

    @Column(nullable = false)
    private String setor;

    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private String sexualidade;

    @Column(nullable = false)
    private boolean termos_de_uso;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
