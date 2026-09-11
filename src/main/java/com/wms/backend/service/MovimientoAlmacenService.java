package com.wms.backend.service;

import com.wms.backend.dto.MovimientoRequestDTO;
import com.wms.backend.dto.MovimientoResponseDTO;

import java.util.List;

public interface MovimientoAlmacenService {
    MovimientoResponseDTO registrarMovimiento(MovimientoRequestDTO requestDTO);
    List<MovimientoResponseDTO> obtenerTodosLosMovimientos();
    List<MovimientoResponseDTO> obtenerKardexPorProducto(Long productoId);
    List<MovimientoResponseDTO> obtenerKardexPorUbicacion(Long ubicacionId);
}