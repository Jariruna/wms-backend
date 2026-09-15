package com.wms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResumenDTO {
    private long totalProductos;
    private long entradasHoy;
    private long salidasHoy;
    private long stockCritico;
    private double porcentajeOcupacion;
    private long productosDisponibles;
    private long productosCriticos;
    private long productosAgotados;
}