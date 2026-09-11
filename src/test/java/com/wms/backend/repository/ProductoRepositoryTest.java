package com.wms.backend.repository;

import com.wms.backend.domain.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private StockUbicacionRepository stockUbicacionRepository; // Inyectado para resolver fk_stock_ubicacion_producto

    @Autowired
    private MovimientoAlmacenRepository movimientoAlmacenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Producto productoEjemplo;
    private String skuUnico;

    @BeforeEach
    void setUp() {
        // 1. Elimina registros de las tablas hijas dependientes en orden inverso
        stockUbicacionRepository.deleteAll();
        movimientoAlmacenRepository.deleteAll();

        // 2. Limpia los registros de la tabla padre
        productoRepository.deleteAll();

        // 3. Genera SKU dinámico por test
        skuUnico = "FLT-IND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        productoEjemplo = Producto.builder()
                .nombre("Filtro Industrial Hidráulico 25µm")
                .descripcion("Filtro de alta eficiencia para maquinaria pesada")
                .codigoSku(skuUnico)
                .precio(new BigDecimal("185.50"))
                .stock(15)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe guardar y recuperar un producto por su ID en PostgreSQL")
    void guardarYBuscarPorId_Exito() {
        Producto productoGuardado = productoRepository.save(productoEjemplo);

        assertThat(productoGuardado.getId()).isNotNull();
        assertThat(productoGuardado.getFechaCreacion()).isNotNull();

        Optional<Producto> productoEncontrado = productoRepository.findById(productoGuardado.getId());
        assertThat(productoEncontrado).isPresent();
        assertThat(productoEncontrado.get().getCodigoSku()).isEqualTo(skuUnico);
    }

    @Test
    @DisplayName("Debe buscar un producto registrado por su código SKU")
    void findByCodigoSku_Exito() {
        entityManager.persistAndFlush(productoEjemplo);

        Optional<Producto> resultado = productoRepository.findByCodigoSku(skuUnico);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Filtro Industrial Hidráulico 25µm");
    }

    @Test
    @DisplayName("Debe listar únicamente los productos con estado activo = true")
    void findByActivoTrue_Exito() {
        String skuInactivo = "EMP-ORG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Producto productoInactivo = Producto.builder()
                .nombre("Empaque O-Ring Neopreno")
                .descripcion("Filtro/Empaque descontinuado")
                .codigoSku(skuInactivo)
                .precio(new BigDecimal("12.00"))
                .stock(0)
                .activo(false)
                .build();

        entityManager.persistAndFlush(productoEjemplo);
        entityManager.persistAndFlush(productoInactivo);

        List<Producto> productosActivos = productoRepository.findByActivoTrue();

        assertThat(productosActivos).hasSize(1);
        // Uso de Java 21+ getFirst()
        assertThat(productosActivos.getFirst().getCodigoSku()).isEqualTo(skuUnico);
    }

    @Test
    @DisplayName("Debe verificar correctamente la existencia de un producto por SKU")
    void existsByCodigoSku_Exito() {
        entityManager.persistAndFlush(productoEjemplo);

        assertThat(productoRepository.existsByCodigoSku(skuUnico)).isTrue();
        assertThat(productoRepository.existsByCodigoSku("SKU-INEXISTENTE-999")).isFalse();
    }
}