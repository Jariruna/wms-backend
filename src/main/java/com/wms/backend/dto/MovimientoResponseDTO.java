package com.wms.backend.dto;

import com.wms.backend.domain.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponseDTO {

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

    // Detalle del Movimiento
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockResultante;
    private String motivo;
    private String usuario;
    private LocalDateTime fechaMovimiento;
}