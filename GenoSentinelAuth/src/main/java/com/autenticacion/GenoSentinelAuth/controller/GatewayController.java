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

    // Metodo para acceder a la URL de la Clínica (sin operaciones)
    @GetMapping("/clinica")
    public ResponseEntity<String> accessClinica(@RequestHeader("Authorization") String token) {
        // Simular la conexión con el microservicio de Clínica
        return simulateRedirection(token, clinicaUrl + "/patients/");
    }

    // Metodo para acceder a la URL de Genómica (sin operaciones)
    @GetMapping("/genomica")
    public ResponseEntity<String> accessGenomica(@RequestHeader("Authorization") String token) {
        // Simular la conexión con el microservicio de Genomica
        return simulateRedirection(token, genomicaUrl + "/gene/");
    }

    // Metodo para simular la redireccion a un microservicio
    private ResponseEntity<String> simulateRedirection(String token, String url) {
        try {
            //Validar el token JWT
            Claims claims = jwt.parse(token);
            String username = claims.getSubject();
            System.out.println("Usuario autenticado: " + username);

            //Aquí simula la conexión al microservicio (sin hacer la petición real)
            System.out.println("Redirigiendo a microservicio en la URL: " + url);

            //Devolver una respuesta que simula la conexion
            return ResponseEntity.status(HttpStatus.OK).body("Conexión simulada con el microservicio en: " + url);

        } catch (Exception e) {
            // Si el token es invalido o expiro, retornar error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}
