package br.com.aeris.aeris_search_config.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "resposta")
public class Resposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime alteradoEm;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resposta;

    @Column(nullable = false)
    private Long perguntaId;

    @Column(nullable = false)
    private Long pesquisaId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 20)
    private String tipoPergunta;

    // Helper methods
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

    public void setRespostaDescritiva(String texto) {
        Map<String, Object> data = new HashMap<>();
        data.put("tipo", "descritiva");
        data.put("texto", texto);
        setJsonDataFromObject(data);
    }

    public void setRespostaEscala(String valor) {
        Map<String, Object> data = new HashMap<>();
        data.put("tipo", "escala");
        data.put("valor", valor);
        setJsonDataFromObject(data);
    }

    public void setRespostaOpcoes(List<String> opcoes) {
        Map<String, Object> data = new HashMap<>();
        data.put("tipo", "opcoes");
        data.put("opcoes", opcoes);
        setJsonDataFromObject(data);
    }
}