package br.com.aeris.aeris_search_config.controller;

import br.com.aeris.aeris_search_config.dto.ErrorResponse;
import br.com.aeris.aeris_search_config.dto.PesquisaResponse;
import br.com.aeris.aeris_search_config.service.PesquisasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pesquisa")
@Tag(name = "Pesquisas", description = "Endpoints para gerenciamento de pesquisas")
public class PesquisasController {
    @Autowired
    private PesquisasService pesquisasService;

    @PostMapping("/criar-pesquisa")
    @Operation(summary = "Criar pesquisa", description = "Endpoint para criar uma nova pesquisa")
    public ResponseEntity<?> createPesquisa(@RequestParam String token) {
        try {
            PesquisaResponse response = pesquisasService.createPesquisa(token);
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

    @GetMapping("/getAllPesquisas")
    @Operation(summary = "Retornar todas pesquisas de uma empresa", description = "Endpoint para retornar todas as pesquisas de uma empresa")
    public ResponseEntity<?> getAllPesquisas(@RequestParam Long empresa) {
        try {
            List<PesquisaResponse> response = pesquisasService.getAllPesquisas(empresa);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            // Não encontrado (404)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

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

    @GetMapping("/retornar-pesquisa")
    @Operation(summary = "Retornar pesquisa", description = "Endpoint para retornar uma pesquisa")
    public ResponseEntity<?> readPesquisa(@RequestParam Long idPesquisa) {
        try {
            PesquisaResponse response = pesquisasService.getPesquisa(idPesquisa);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            // Não encontrado (404)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

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

    @GetMapping("/finalizar-pesquisa")
    @Operation(summary = "Finalizar pesquisa", description = "Endpoint para finalizar uma pesquisa")
    public ResponseEntity<?> finalizarPesquisa(@RequestParam Long idPesquisa) {
        try {
            PesquisaResponse response = pesquisasService.finalizarPesquisa(idPesquisa);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Erro de validação (400)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())

                    .build();
            return ResponseEntity.badRequest().body(error);

        } catch (EntityNotFoundException e) {
            // Não encontrado (404)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

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
}
