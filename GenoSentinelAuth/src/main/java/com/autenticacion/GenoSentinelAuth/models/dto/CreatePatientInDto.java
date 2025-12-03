package com.autenticacion.GenoSentinelAuth.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class CreatePatientInDto {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String gender;
}
