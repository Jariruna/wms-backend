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
public class StockUbicacionResponseDTO {

    private Long id;

    // Datos del Producto
    private Long productoId;
    private String productoCodigoSku;
    private String productoNombre;

    // Datos de la Ubicación Física
    private Long ubicacionId;
    private String ubicacionCodigo;
    private String ubicacionZona;
    private String ubicacionPasillo;
    private String ubicacionRack;
    private String ubicacionNivel;

    // Cantidad en la ubicación específica
    private Integer cantidad;
    private LocalDateTime fechaActualizacion;
}