package br.com.aeris.aeris_search_config.controller;

import br.com.aeris.aeris_search_config.dto.RespostaRequest;
import br.com.aeris.aeris_search_config.dto.RespostaResponse;
import br.com.aeris.aeris_search_config.model.Resposta;
import br.com.aeris.aeris_search_config.service.RespostaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resposta")
@Tag(name = "Respostas", description = "Endpoints para gerenciamento de respostas")
public class RespostaController {
    @Autowired
    private RespostaService respostaService;

    @PostMapping("/submter")
    public ResponseEntity<RespostaResponse> submeterRespostas(
            @Valid @RequestBody RespostaRequest request) {

        RespostaResponse response = respostaService.salvarRespostas(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pesquisa/{pesquisaId}/usuario/{usuarioId}")
    public ResponseEntity<List<Resposta>> buscarRespostas(
            @PathVariable Long pesquisaId,
            @PathVariable Long usuarioId) {

        List<Resposta> respostas = respostaService
                .buscarRespostasPorPesquisaEUsuario(pesquisaId, usuarioId);

        return ResponseEntity.ok(respostas);
    }
}
