package br.com.aeris.aeris_search_config.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/keep")
@Tag(name = "Keep Alive", description = "KeepAliveController")
public class KeepAliveController {

    @GetMapping("/alive")
    public Map<String, Object> keepAlive() {
        return Map.of(
                "status", "OK",
                "timestamp", LocalDateTime.now().toString(),
                "message", "Serviço ativo e respondendo."
        );
    }
}
