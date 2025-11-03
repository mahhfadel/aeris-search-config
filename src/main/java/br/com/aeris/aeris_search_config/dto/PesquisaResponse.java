package br.com.aeris.aeris_search_config.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PesquisaResponse {
    private String nome;
    private Long idPesquisa;
    private LocalDate criadoEm;
    private LocalDateTime finalizadoEm;
    private LocalDate prazo;
    private Boolean ativo;
    private Long totalUsuarios;
    private Long respondidos;
    private String mensagem;
}
