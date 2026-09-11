package com.wms.backend.repository;

import com.wms.backend.domain.UbicacionAlmacen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UbicacionRepositoryTest {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private String codigoUnico;

    @BeforeEach
    void setUp() {
        // Limpieza de datos previa para garantizar el aislamiento entre ejecuciones en PostgreSQL
        ubicacionRepository.deleteAll();

        codigoUnico = "A-01-1-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    @Test
    @DisplayName("Debe guardar y consultar una ubicación por su código único")
    void findByCodigoUbicacion_Exito() {
        UbicacionAlmacen ubicacion = UbicacionAlmacen.builder()
                .codigoUbicacion(codigoUnico)
                .pasillo("A")
                .rack("01")
                .nivel("1")
                .posicion("01")
                .capacidadMaxima(200)
                .activa(true)
                .build();

        entityManager.persistAndFlush(ubicacion);

        Optional<UbicacionAlmacen> resultado = ubicacionRepository.findByCodigoUbicacion(codigoUnico);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPasillo()).isEqualTo("A");
    }

    @Test
    @DisplayName("Debe listar únicamente las ubicaciones pertenecientes a un pasillo activo")
    void findByPasilloAndActivaTrue_Exito() {
        String pasilloTarget = "DESPACHO";

        UbicacionAlmacen u1 = UbicacionAlmacen.builder()
                .codigoUbicacion("DSP-01-" + UUID.randomUUID().toString().substring(0, 4))
                .pasillo(pasilloTarget)
                .rack("01")
                .nivel("1")
                .posicion("01")
                .activa(true)
                .build();

        UbicacionAlmacen u2 = UbicacionAlmacen.builder()
                .codigoUbicacion("DSP-02-" + UUID.randomUUID().toString().substring(0, 4))
                .pasillo(pasilloTarget)
                .rack("01")
                .nivel("1")
                .posicion("02")
                .activa(false) // Inactiva
                .build();

        entityManager.persistAndFlush(u1);
        entityManager.persistAndFlush(u2);

        List<UbicacionAlmacen> activasEnPasillo = ubicacionRepository.findByPasilloAndActivaTrue(pasilloTarget);

        assertThat(activasEnPasillo).hasSize(1);
        assertThat(activasEnPasillo.getFirst().getActiva()).isTrue();
    }
}