package com.autenticacion.GenoSentinelAuth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    // Indica que el servicio está vivo
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    // Endpoint de estado: mas informacion del servicio
    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "service", "GenoSentinel Authentication & Gateway",
                "status", "RUNNING",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
