package com.autenticacion.GenoSentinelAuth.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class CreateClinicalRecordInDto {
    private String patientId;
    private String tumorTypeId;
    private String diagnosisDate;
    private String stage;
    private String treatmentProtocol;
}
