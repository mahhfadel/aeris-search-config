package br.com.aeris.aeris_search_config.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColaboradorResponse {
    private Long id;
    private String nome;
    private String email;
    private String genero;
    private String setor;
    private String cargo;
    private String tempoDeCasa;
    private Long respondidos;
    private Long total;
    private String mensagem;
}
