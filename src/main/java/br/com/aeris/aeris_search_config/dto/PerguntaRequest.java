package br.com.aeris.aeris_search_config.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerguntaRequest {
    private String pergunta;
    private String adjetivo;
    private Long pesquisaId;
    private String tipoPergunta;
    private List<OpcoesRequest> opcoes;
}
