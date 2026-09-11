package com.wms.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionRequestDTO {

    @NotBlank(message = "El código de ubicación es obligatorio")
    @Size(max = 30, message = "El código no debe superar los 30 caracteres")
    private String codigoUbicacion;

    @NotBlank(message = "El pasillo es obligatorio")
    @Size(max = 10, message = "El pasillo no debe superar los 10 caracteres")
    private String pasillo;

    @NotBlank(message = "El rack es obligatorio")
    @Size(max = 10, message = "El rack no debe superar los 10 caracteres")
    private String rack;

    @NotBlank(message = "El nivel es obligatorio")
    @Size(max = 10, message = "El nivel no debe superar los 10 caracteres")
    private String nivel;

    @Size(max = 10, message = "La posición no debe superar los 10 caracteres")
    private String posicion;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
    private Integer capacidadMaxima;
}