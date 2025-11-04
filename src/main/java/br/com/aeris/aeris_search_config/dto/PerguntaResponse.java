package br.com.aeris.aeris_search_config.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PerguntaResponse {
    private Long id;
    private String pergunta;
    private String adjetivo;
    private String mensagem;
    private TipoPerguntaResponse tipoPergunta;
}
