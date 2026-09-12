package com.wms.backend.service;

import com.wms.backend.domain.TipoMovimiento;
import com.wms.backend.dto.DashboardResumenDTO;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.MovimientoAlmacenRepository; // O la entidad correspondiente a tu Kardex
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductoRepository productoRepository;
    // Inyecta el repositorio de movimientos o kardex si ya lo tienes definido
    private final MovimientoAlmacenRepository movimientoRepository;

    public DashboardResumenDTO obtenerResumen() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);

        long totalProductos = productoRepository.count();

        // Consultas de ejemplo usando tu repositorio de movimientos/kardex:
        long entradasHoy = movimientoRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                TipoMovimiento.ENTRADA, inicioHoy, finHoy);
        long salidasHoy = movimientoRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                TipoMovimiento.SALIDA, inicioHoy, finHoy);

        return DashboardResumenDTO.builder()
                .totalProductos(totalProductos)
                .entradasHoy(entradasHoy) // Reemplazar por entradasHoy al mapear tu repositorio
                .salidasHoy(salidasHoy)  // Reemplazar por salidasHoy al mapear tu repositorio
                .build();
    }
}