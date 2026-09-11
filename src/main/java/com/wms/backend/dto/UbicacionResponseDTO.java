package com.wms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionResponseDTO {

    private Long id;
    private String codigoUbicacion;
    private String pasillo;
    private String rack;
    private String nivel;
    private String posicion;
    private Integer capacidadMaxima;
    private Boolean ocupada;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
}