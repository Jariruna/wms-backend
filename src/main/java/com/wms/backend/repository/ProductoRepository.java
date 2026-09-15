package com.wms.backend.repository;

import com.wms.backend.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoSku(String codigoSku);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.ubicacion WHERE p.activo = true")
    List<Producto> findByActivoTrue();

    boolean existsByCodigoSku(String codigoSku);

    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosConStockMinimo();

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.ubicacion")
    List<Producto> findAll();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    long contarStockCritico();
}