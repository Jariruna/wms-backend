package com.wms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockUbicacionDTO {
    private Long id;
    private Long productoId;
    private String productoCodigoSku;
    private String productoNombre;
    private Long ubicacionId;
    private String ubicacionCodigo;
    private String zona;
    private String descripcion;
    private Integer stock;


}