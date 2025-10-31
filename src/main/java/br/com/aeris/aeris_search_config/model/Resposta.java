package br.com.aeris.aeris_search_config.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Column(columnDefinition = "TEXT")
    private String resposta;

    @Column(nullable = false)
    private Long pergunta_id;

    @Column(nullable = false)
    private Long pesquisa_id;

    @Column(nullable = false)
    private Long usuario_id;

    @Column(nullable = false)
    private LocalDateTime alterado_em;

    @Transient
    public JsonNode getJsonDataAsNode() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(resposta);
        } catch (Exception e) {
            return null;
        }
    }

    public void setJsonDataFromObject(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.resposta = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            this.resposta = null;
        }
    }
}
