package br.com.aeris.aeris_search_config.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TipoPerguntaResponse {
    private Long id;
    private String descricao;
    private List<OpcoesResponse> opcoes;
}
