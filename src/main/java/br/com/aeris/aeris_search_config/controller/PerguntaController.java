package br.com.aeris.aeris_search_config.controller;

import br.com.aeris.aeris_search_config.dto.*;
import br.com.aeris.aeris_search_config.service.PerguntaService;
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
@RequestMapping("/api/pergunta")
@Tag(name = "Perguntas", description = "Endpoints para gerenciamento de perguntas")
public class PerguntaController {
    @Autowired
    private PerguntaService perguntaService;

    @PostMapping("/adicionar-pergunta")
    @Operation(summary = "Adicionar pergunta", description = "Endpoint para adicionar novas perguntas")
    public ResponseEntity<?> createPergunta(@RequestBody PerguntaRequest request) {
        try {
            PerguntaResponse response = perguntaService.adicionarPergunta(request);
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

    @PatchMapping("/atualizar-perunta")
    @Operation(summary = "Finalizar pesquisa", description = "Endpoint para finalizar uma pesquisa")
    public ResponseEntity<?> atualizarPergunta(@RequestParam Long idPergunta, @RequestBody PerguntaRequest request) {
        try {
            PerguntaResponse response = perguntaService.atualizarPergunta(idPergunta, request);
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

    @GetMapping("/getAll")
    @Operation(summary = "Retornar todas as pergunta de uma pesquisa", description = "Endpoint para retornar todas as pergunta de uma pesquisa")
    public ResponseEntity<?> getAllPerguntas(@RequestParam Long pesquisa) {
        try {
            List<PerguntaResponse> response = perguntaService.getAllPerguntas(pesquisa);
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

    @PatchMapping("/deletar-perunta")
    @Operation(summary = "Finalizar pesquisa", description = "Endpoint para finalizar uma pesquisa")
    public ResponseEntity<?> deletarPergunta(@RequestParam Long idPergunta) {
        try {
            PerguntaResponse response = perguntaService.deletarPergunta(idPergunta);
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
}
