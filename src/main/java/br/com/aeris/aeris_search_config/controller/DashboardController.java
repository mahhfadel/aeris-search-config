package br.com.aeris.aeris_search_config.controller;

import br.com.aeris.aeris_search_config.dto.DashboardResponse;
import br.com.aeris.aeris_search_config.dto.DataDashboardResponse;
import br.com.aeris.aeris_search_config.dto.ErrorResponse;
import br.com.aeris.aeris_search_config.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoints para trazer contagens dos valores da dash")
public class DashboardController {

    @Autowired
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashGeral")
    public ResponseEntity<?> getDashboardData() {
        try {
            List<DashboardResponse> response = dashboardService.getDashboardData();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro interno no servidor")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/dashPesquisa")
    public ResponseEntity<?> getDashboardPesquisa(@RequestParam Long pesquisaId) {
        try {
            List<DashboardResponse> response = dashboardService.getDashboardPesquisa(pesquisaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro interno no servidor")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
