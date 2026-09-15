package com.wms.backend.service;

import com.wms.backend.domain.TipoMovimiento;
import com.wms.backend.dto.MovimientoRequestDTO;
import com.wms.backend.dto.MovimientoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoAlmacenService {
    MovimientoResponseDTO registrarMovimiento(MovimientoRequestDTO requestDTO);
    List<MovimientoResponseDTO> obtenerTodosLosMovimientos();
    List<MovimientoResponseDTO> obtenerKardexPorProducto(Long productoId);
    List<MovimientoResponseDTO> obtenerKardexPorUbicacion(Long ubicacionId);

    // Nuevo método para listar movimientos aplicando filtros avanzados
    List<MovimientoResponseDTO> obtenerMovimientosFiltrados(
            Long productoId,
            TipoMovimiento tipo,
            Long ubicacionId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}