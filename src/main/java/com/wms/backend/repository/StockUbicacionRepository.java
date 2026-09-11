package com.wms.backend.repository;

import com.wms.backend.domain.StockUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockUbicacionRepository extends JpaRepository<StockUbicacion, Long> {

    Optional<StockUbicacion> findByProductoIdAndUbicacionId(Long productoId, Long ubicacionId);

    List<StockUbicacion> findByProductoId(Long productoId);

    List<StockUbicacion> findByUbicacionId(Long ubicacionId);
}