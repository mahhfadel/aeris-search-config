package br.com.aeris.aeris_search_config.controller;

import br.com.aeris.aeris_search_config.dto.ColaboradorResponse;
import br.com.aeris.aeris_search_config.dto.ErrorResponse;
import br.com.aeris.aeris_search_config.repository.PesquisaColaboradorRepository;
import br.com.aeris.aeris_search_config.service.ColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/colaborador")
@Tag(name = "Colaboradores", description = "Endpoints para gerenciamento de colaboradores")
public class ColaboradorController {
    @Autowired
    private ColaboradorService colaboradorService;

    @Autowired
    private PesquisaColaboradorRepository pesquisaColaboradorRepository;

    @PostMapping("/adicionar")
    @Operation(summary = "Adicionar colaboradores a uma pesquisa", description = "Endpoint para adicionar colaboradores a uma pesquisa")
    public ResponseEntity<?> adiconarColaboradores(@RequestBody List<Long> colaboradores, @RequestParam Long pesquisaId) {
        try {
            ColaboradorResponse response = colaboradorService.adicionarColaborador(colaboradores, pesquisaId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Erro de validação (400)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())

                    .build();
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            // Erro genérico (500)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro interno no servidor")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/allUsuario")
    @Operation(summary = "Retornar todos usuários", description = "Endpoint para retornar todos os usuários")
    public ResponseEntity<?> getAllColaboraderesUsers(@RequestParam Long empresa, Long pesquisa) {
        try {
            List<ColaboradorResponse> response = colaboradorService.getAllColaboradoresUsers(empresa, pesquisa);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .path("/api/usuarios/allUsuario")
                    .build();

            return ResponseEntity.badRequest().body(error);
        }
    }
}
