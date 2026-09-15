package com.wms.backend.service;

import com.wms.backend.domain.TipoMovimiento;
import com.wms.backend.dto.DashboardResumenDTO;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.MovimientoAlmacenRepository;
import com.wms.backend.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final MovimientoAlmacenRepository movimientoRepository;
    private final UbicacionRepository ubicacionRepository;

    public DashboardResumenDTO obtenerResumen() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);

        long totalProductos = productoRepository.count();

        long entradasHoy = movimientoRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                TipoMovimiento.ENTRADA, inicioHoy, finHoy);
        long salidasHoy = movimientoRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                TipoMovimiento.SALIDA, inicioHoy, finHoy);

        long stockCritico = productoRepository.contarStockCritico();

        // 1. Obtenemos la capacidad máxima total acumulada solo de los almacenes/ubicaciones activos
        Long totalCapacidadMaxima = ubicacionRepository.sumarCapacidadMaximaActiva();

        // 2. Sumamos el stock real actual de todos los productos registrados en el sistema
        long stockTotalActual = productoRepository.findAll().stream()
                .mapToInt(p -> p.getStock() != null ? p.getStock() : 0)
                .sum();

        // 3. Cálculo del porcentaje volumétrico real, progresivo e independiente de la cantidad de almacenes
        double porcentajeOcupacion = 0.0;
        if (totalCapacidadMaxima != null && totalCapacidadMaxima > 0) {
            porcentajeOcupacion = Math.round(((double) stockTotalActual / totalCapacidadMaxima) * 100.0 * 10.0) / 10.0;

            // Límite de seguridad visual para evitar que rebase el 100% si el stock supera temporalmente la capacidad
            if (porcentajeOcupacion > 100.0) {
                porcentajeOcupacion = 100.0;
            }
        }

        // 4. Conteo de productos por estado de stock para el gráfico de dona
        long productosAgotados = productoRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() == 0)
                .count();

        long productosCriticos = productoRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0 && p.getStock() <= (p.getStockMinimo() != null ? p.getStockMinimo() : 0))
                .count();

        long productosDisponibles = productoRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() > (p.getStockMinimo() != null ? p.getStockMinimo() : 0))
                .count();

        return DashboardResumenDTO.builder()
                .totalProductos(totalProductos)
                .entradasHoy(entradasHoy)
                .salidasHoy(salidasHoy)
                .stockCritico(stockCritico)
                .porcentajeOcupacion(porcentajeOcupacion)
                .productosDisponibles(productosDisponibles)
                .productosCriticos(productosCriticos)
                .productosAgotados(productosAgotados)
                .build();
    }
}