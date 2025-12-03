package com.autenticacion.GenoSentinelAuth.controller;

import com.autenticacion.GenoSentinelAuth.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/gateway/genomica")
@RequiredArgsConstructor
@Tag(name = "Genomic Gateway", description = "Gateway hacia el microservicio genómico")
public class GenomicaGatewayController {

    private final JwtService jwt;
    private final ApiClient apiClient;

    @Value("${microservicio.genomica.url}")
    private String genomicaUrl;

    // -------------------------------
    // Helpers
    // -------------------------------
    private ResponseEntity<?> success(String body) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private ResponseEntity<?> error(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "error", "Error contacting genomic microservice",
                        "message", e.getMessage(),
                        "status", 502
                ));
    }

    // =========================================================================
    //                              GENES
    // =========================================================================

    @Operation(
            summary = "Crear un gene",
            description = "Crea un nuevo gene en el microservicio genómico."
    )
    @PostMapping("/gene")
    public ResponseEntity<?> createGene(@RequestBody String body) {
        try {
            String url = genomicaUrl + "/gene/";
            return success(apiClient.post(url, body));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Listar todos los genes",
            description = "Obtiene la lista completa de genes."
    )
    @GetMapping("/gene")
    public ResponseEntity<?> getAllGenes() {
        try {
            String url = genomicaUrl + "/gene/";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Obtener gene por ID",
            description = "Devuelve un gene según su ID."
    )
    @GetMapping("/gene/{id}")
    public ResponseEntity<?> getGeneById(@PathVariable String id) {
        try {
            String url = genomicaUrl + "/gene/" + id + "/";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Actualizar gene",
            description = "Modifica atributos de un gene específico."
    )
    @PatchMapping("/gene/{id}")
    public ResponseEntity<?> updateGene(@PathVariable String id, @RequestBody String body) {
        try {
            String url = genomicaUrl + "/gene/" + id + "/";
            return success(apiClient.patch(url, body));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Eliminar gene",
            description = "Elimina un gene por ID."
    )
    @DeleteMapping("/gene/{id}")
    public ResponseEntity<?> deleteGene(@PathVariable String id) {
        try {
            String url = genomicaUrl + "/gene/" + id + "/";
            return success(apiClient.delete(url));
        } catch (Exception e) {
            return error(e);
        }
    }


    // =========================================================================
    //                        VARIANTES GENÉTICAS
    // =========================================================================

    @Operation(
            summary = "Crear variante genética",
            description = "Crea una variante genética asociada a un gene."
    )
    @PostMapping("/genetic-variants")
    public ResponseEntity<?> createVariant(@RequestBody String body) {
        try {
            String url = genomicaUrl + "/genetic-variants/";
            return success(apiClient.post(url, body));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Listar variantes genéticas",
            description = "Obtiene todas las variantes genéticas."
    )
    @GetMapping("/genetic-variants")
    public ResponseEntity<?> getAllVariants() {
        try {
            String url = genomicaUrl + "/genetic-variants/";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Obtener variante por UUID",
            description = "Consulta una variante genética por su UUID."
    )
    @GetMapping("/genetic-variants/{uuid}")
    public ResponseEntity<?> getVariant(@PathVariable String uuid) {
        try {
            String url = genomicaUrl + "/genetic-variants/" + uuid + "/";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Actualizar variante genética",
            description = "Modifica una variante genética específica."
    )
    @PatchMapping("/genetic-variants/{uuid}")
    public ResponseEntity<?> updateVariant(@PathVariable String uuid, @RequestBody String body) {
        try {
            String url = genomicaUrl + "/genetic-variants/" + uuid + "/";
            return success(apiClient.patch(url, body));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Eliminar variante genética",
            description = "Elimina una variante genética usando su UUID."
    )
    @DeleteMapping("/genetic-variants/{uuid}")
    public ResponseEntity<?> deleteVariant(@PathVariable String uuid) {
        try {
            String url = genomicaUrl + "/genetic-variants/" + uuid + "/";
            return success(apiClient.delete(url));
        } catch (Exception e) {
            return error(e);
        }
    }


    // =========================================================================
    //                               PACIENTES
    // =========================================================================

    @Operation(
            summary = "Crear paciente",
            description = "Registra un nuevo paciente."
    )
    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody String body) {
        try {
            String url = genomicaUrl + "/patients/";
            return success(apiClient.post(url, body));
        } catch (Exception e) {
            return error(e);
        }
    }


    // =========================================================================
    //                   ASIGNACIÓN DE VARIANTES A PACIENTES
    // =========================================================================

    @Operation(
            summary = "Asignar variante genética a un paciente",
            description = "Crea un reporte que relaciona paciente + variante genética."
    )
    @PostMapping("/assign-genetic-variant")
    public ResponseEntity<?> assignVariant(@RequestBody String body) {
        try {
            String url = genomicaUrl + "/assign-genetic-variant/";
            return success(apiClient.post(url, body));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Listar reportes de variantes por paciente",
            description = "Muestra los reportes que vinculan variantes con pacientes."
    )
    @GetMapping("/patient-variant-reports")
    public ResponseEntity<?> listVariantReports() {
        try {
            String url = genomicaUrl + "/patient-variant-reports/";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }

}
