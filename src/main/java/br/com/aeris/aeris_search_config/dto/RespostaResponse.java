package br.com.aeris.aeris_search_config.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RespostaResponse {
    private Long pesquisaId;
    private Long usuarioId;
    private Integer totalRespostas;
    private LocalDateTime submissaoEm;
    private String status;
}