package com.wms.backend.repository;

import com.wms.backend.domain.UbicacionAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<UbicacionAlmacen, Long> {

    // Validaciones y búsquedas por código único
    boolean existsByCodigoUbicacion(String codigoUbicacion);

    Optional<UbicacionAlmacen> findByCodigoUbicacion(String codigoUbicacion);

    // Búsquedas generales por estado
    List<UbicacionAlmacen> findByActivaTrue();

    List<UbicacionAlmacen> findByOcupadaAndActivaTrue(Boolean ocupada);

    // Búsquedas por zona
    List<UbicacionAlmacen> findByZona(String zona);

    List<UbicacionAlmacen> findByZonaAndActivaTrue(String zona);

    // Búsquedas por pasillo y rack
    List<UbicacionAlmacen> findByPasilloAndActivaTrue(String pasillo);

    List<UbicacionAlmacen> findByPasilloAndRackAndActivaTrue(String pasillo, String rack);

    // Nuevos métodos para el cálculo de capacidad y ocupación en el Dashboard
    long countByActivaTrue();

    long countByOcupadaTrueAndActivaTrue();

    @Query("SELECT SUM(u.capacidadMaxima) FROM UbicacionAlmacen u WHERE u.activa = true")
    Long sumarCapacidadMaximaActiva();
}