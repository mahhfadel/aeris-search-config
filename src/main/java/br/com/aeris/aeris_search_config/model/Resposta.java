package br.com.aeris.aeris_user_config.model;

import io.swagger.v3.core.util.Json;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resposta")
public class Resposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criado_em;

    @Column(nullable = false)
    private Json resposta;

    @Column(nullable = false)
    private Long pergunta_id;

    @Column(nullable = false)
    private Long pesquisa_id;

    @Column(nullable = false)
    private Long usuario_id;

    @Column(nullable = false)
    private LocalDateTime alterado_em;
}
