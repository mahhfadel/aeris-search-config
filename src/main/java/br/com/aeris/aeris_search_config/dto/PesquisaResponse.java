package br.com.aeris.aeris_search_config.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PesquisaResponse {
    private String nome;
    private Long idPesquisa;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate criadoEm;

    private LocalDateTime finalizadoEm;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate prazo;

    private Boolean ativo;
    private Long totalUsuarios;
    private Long respondidos;
    private String mensagem;
}
