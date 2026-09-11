package com.wms.backend.repository;

import com.wms.backend.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoSku(String codigoSku);

    List<Producto> findByActivoTrue();

    boolean existsByCodigoSku(String codigoSku);
}