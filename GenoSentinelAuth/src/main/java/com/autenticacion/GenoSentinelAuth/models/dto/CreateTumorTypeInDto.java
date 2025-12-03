package com.autenticacion.GenoSentinelAuth.models.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class CreateTumorTypeInDto {
    private String name;
    private String systemAffected;
}
