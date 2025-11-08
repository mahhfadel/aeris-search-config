package br.com.aeris.aeris_search_config.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaItemRequest {
    @NotNull
    private Long perguntaId;

    @NotNull
    private String tipoPergunta;

    private String respostaDescritiva;

    private String respostaEscala;

    private List<String> respostaOpcoes;
}
