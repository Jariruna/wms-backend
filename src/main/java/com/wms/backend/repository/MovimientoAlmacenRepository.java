package com.wms.backend.repository;

import com.wms.backend.domain.MovimientoAlmacen;
import com.wms.backend.domain.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoAlmacenRepository extends JpaRepository<MovimientoAlmacen, Long> {

    // Historial de Kardex ordenado por fecha descendente
    List<MovimientoAlmacen> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);

    // Consulta de trazabilidad por posición física en el almacén
    List<MovimientoAlmacen> findByUbicacionIdOrderByFechaMovimientoDesc(Long ubicacionId);

    long countByTipoMovimientoAndFechaMovimientoBetween(TipoMovimiento tipoMovimiento, LocalDateTime inicio, LocalDateTime fin);
}