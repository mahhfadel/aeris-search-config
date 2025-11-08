package br.com.aeris.aeris_search_config.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RespostaRequest {
    @NotNull
    private Long pesquisaId;

    @NotNull
    private String tokenUser;

    @NotEmpty
    @Valid
    private List<RespostaItemRequest> respostas;
}
