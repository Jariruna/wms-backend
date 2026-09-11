package com.wms.backend.repository;

import com.wms.backend.domain.UbicacionAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<UbicacionAlmacen, Long> {

    // AGREGAR ESTAS DOS LÍNEAS:
    boolean existsByCodigoUbicacion(String codigoUbicacion);

    Optional<UbicacionAlmacen> findByCodigoUbicacion(String codigoUbicacion);

    // MÉTODOS EXISTENTES EN TU IMAGEN:
    List<UbicacionAlmacen> findByActivaTrue();

    List<UbicacionAlmacen> findByOcupadaAndActivaTrue(Boolean ocupada);

    List<UbicacionAlmacen> findByPasilloAndRackAndActivaTrue(String pasillo, String rack);

    List<UbicacionAlmacen> findByPasilloAndActivaTrue(String pasillo);
}