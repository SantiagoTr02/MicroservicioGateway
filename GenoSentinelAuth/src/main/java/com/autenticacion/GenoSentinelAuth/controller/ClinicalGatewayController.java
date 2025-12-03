package com.autenticacion.GenoSentinelAuth.controller;

import com.autenticacion.GenoSentinelAuth.models.dto.CreateClinicalRecordInDto;
import com.autenticacion.GenoSentinelAuth.models.dto.CreatePatientInDto;
import com.autenticacion.GenoSentinelAuth.models.dto.CreateTumorTypeInDto;
import com.autenticacion.GenoSentinelAuth.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/gateway/clinical")
@RequiredArgsConstructor
@Tag(name = "Clinical Gateway", description = "Gateway hacia el microservicio clínico")
public class ClinicalGatewayController {

    private final JwtService jwt;
    private final ApiClient apiClient;

    @Value("${microservicio.clinica.url}")
    private String clinicaUrl;

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
                        "error", "Error contacting clinical microservice",
                        "message", e.getMessage(),
                        "status", 502
                ));
    }

    // -------------------------------
    // ENDPOINTS
    // -------------------------------

    @Operation(
            summary = "Obtener lista de pacientes",
            description = "Consulta todos los pacientes almacenados en el microservicio clínico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de pacientes", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "502", description = "Error comunicándose con el microservicio clínico")
            }
    )
    @GetMapping("/patients")
    public ResponseEntity<?> findAllPatients() {
        try {
            String url = clinicaUrl + "/patients";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }


    @Operation(
            summary = "Crear un paciente",
            description = "Crea un nuevo paciente en el microservicio clínico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paciente creado"),
                    @ApiResponse(responseCode = "502", description = "Error comunicándose con el microservicio clínico")
            }
    )
    @PostMapping("/patient")
    public ResponseEntity<?> createPatient(@RequestBody CreatePatientInDto dto) {
        try {
            String url = clinicaUrl + "/patients";
            return success(apiClient.post(url, dto));
        } catch (Exception e) {
            return error(e);
        }
    }


    @Operation(
            summary = "Crear tipo de tumor",
            description = "Crea un nuevo tipo de tumor en el microservicio clínico"
    )
    @PostMapping("/tumortype")
    public ResponseEntity<?> createTumorType(@RequestBody CreateTumorTypeInDto dto) {
        try {
            String url = clinicaUrl + "/tumortypes";
            return success(apiClient.post(url, dto));
        } catch (Exception e) {
            return error(e);
        }
    }


    @Operation(
            summary = "Crear una historia clínica",
            description = "Crea un registro clínico para un paciente"
    )
    @PostMapping("/clinicalrecord")
    public ResponseEntity<?> createClinicalRecord(@RequestBody CreateClinicalRecordInDto dto) {
        try {
            String url = clinicaUrl + "/clinicalrecords";
            return success(apiClient.post(url, dto));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(

    )
    @GetMapping("/tumortypes")
    public ResponseEntity<?> getAllTumorTypes() {
        try {
            String url = clinicaUrl + "/tumortypes";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Obtener todos los registros clínicos",
            description = "Llama al microservicio clínico para obtener el listado completo de clinicalrecords."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "502", description = "Error comunicándose con el microservicio clínico")
    })
    @GetMapping("/clinicalrecords")
    public ResponseEntity<?> getAllClinicalRecords() {
        try {
            String url = clinicaUrl + "/clinicalrecords";
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }



    @Operation(
            summary = "Cambiar el estado de un paciente",
            description = "Actualiza el estado (activo/inactivo) de un paciente en el microservicio clínico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
            @ApiResponse(responseCode = "502", description = "Error comunicándose con el microservicio clínico")
    })
    @PatchMapping("/patients/{idPatient}/status")
    public ResponseEntity<?> statusPatient(@PathVariable String idPatient) {
        try {
            String url = clinicaUrl + "/patients/" + idPatient + "/status";
            return success(apiClient.patch(url));
        } catch (Exception e) {
            return error(e);
        }
    }

    @Operation(
            summary = "Obtener un paciente por ID",
            description = "Devuelve la información detallada de un paciente específico consultando el microservicio clínico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "El paciente no existe"),
            @ApiResponse(responseCode = "502", description = "Error comunicándose con el microservicio clínico")
    })
    @GetMapping("/patients/{idPatient}")
    public ResponseEntity<?> findById(
            @Parameter(description = "ID del paciente a consultar", required = true)
            @PathVariable String idPatient
    ) {
        try {
            String url = clinicaUrl + "/patients/" + idPatient;
            return success(apiClient.get(url));
        } catch (Exception e) {
            return error(e);
        }
    }

}
