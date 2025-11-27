package com.autenticacion.GenoSentinelAuth.controller;

import com.autenticacion.GenoSentinelAuth.services.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class GatewayController {

    private final JwtService jwt;

    @Value("${microservicio.clinica.url}")
    private String clinicaUrl;

    @Value("${microservicio.genomica.url}")
    private String genomicaUrl;

    // Método para acceder a la URL de la Clínica (sin operaciones)
    @GetMapping("/clinica")
    public ResponseEntity<String> accessClinica(@RequestHeader("Authorization") String token) {
        // Simular la conexión con el microservicio de Clínica
        return simulateRedirection(token, clinicaUrl + "/patients/");
    }

    // Método para acceder a la URL de Genómica (sin operaciones)
    @GetMapping("/genomica")
    public ResponseEntity<String> accessGenomica(@RequestHeader("Authorization") String token) {
        // Simular la conexión con el microservicio de Genómica
        return simulateRedirection(token, genomicaUrl + "/gene/");
    }

    // Método para simular la redirección a un microservicio
    private ResponseEntity<String> simulateRedirection(String token, String url) {
        try {
            // 1. Validar el token JWT
            Claims claims = jwt.parse(token);
            String username = claims.getSubject();
            System.out.println("Usuario autenticado: " + username);  // Imprime el usuario autenticado

            // 2. Aquí simula la conexión al microservicio (sin hacer la petición real)
            System.out.println("Redirigiendo a microservicio en la URL: " + url);

            // 3. Devolver una respuesta que simula la conexión
            return ResponseEntity.status(HttpStatus.OK).body("Conexión simulada con el microservicio en: " + url);

        } catch (Exception e) {
            // Si el token es inválido o expiró, retornar error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}
