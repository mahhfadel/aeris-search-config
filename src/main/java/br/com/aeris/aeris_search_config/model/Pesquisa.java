package br.com.aeris.aeris_search_config.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pesquisa")
public class Pesquisa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate criadoEm;

    @Column(nullable = false)
    private LocalDateTime FinalizadoEm;

    @Column(nullable = false)
    private LocalDate prazo;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Long usuarioId;

    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
