package ar.edu.unq.pdss22025.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "health-controller", description = "Endpoint de health check y ping")
public class HealthController {

    @GetMapping("/ping")
    @Operation(summary = "Ping", description = "Endpoint de health check que devuelve el estado del servidor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servidor funcionando correctamente")
    })
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "pong");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "compra-tu-auto-backend");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Health check", description = "Endpoint de health check detallado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servidor funcionando correctamente")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "compra-tu-auto-backend");
        return ResponseEntity.ok(response);
    }
}

