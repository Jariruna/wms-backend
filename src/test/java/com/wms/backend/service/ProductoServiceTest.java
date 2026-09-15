package com.wms.backend.service;

import com.wms.backend.domain.Producto;
import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;
import com.wms.backend.exception.DuplicateSkuException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.mapper.ProductoMapper;
import com.wms.backend.mapper.UbicacionMapper;
import com.wms.backend.repository.MovimientoAlmacenRepository;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.StockUbicacionRepository;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UbicacionRepository ubicacionRepository;

    @Mock
    private MovimientoAlmacenRepository movimientoAlmacenRepository;

    @Mock
    private StockUbicacionRepository stockUbicacionRepository;

    // Instanciación directa del Mapper asegurando sus dependencias sin reflexión fallida
    @Spy
    private ProductoMapper productoMapper = new ProductoMapper(new UbicacionMapper());

    private ProductoServiceImpl productoService;

    private ProductoRequestDTO requestDTO;
    private Producto productoEntity;

    @BeforeEach
    void setUp() {
        // Inicializamos el servicio manualmente pasando las cinco dependencias en el orden correcto
        productoService = new ProductoServiceImpl(
                productoRepository,
                ubicacionRepository,
                movimientoAlmacenRepository,
                productoMapper,
                stockUbicacionRepository

        );

        requestDTO = ProductoRequestDTO.builder()
                .nombre("Filtro Industrial Hidráulico 25µm")
                .descripcion("Filtro de alta eficiencia para maquinaria pesada")
                .codigoSku("FLT-IND-001")
                .precio(new BigDecimal("185.50"))
                .stock(10)
                .build();

        productoEntity = Producto.builder()
                .id(1L)
                .nombre("Filtro Industrial Hidráulico 25µm")
                .descripcion("Filtro de alta eficiencia para maquinaria pesada")
                .codigoSku("FLT-IND-001")
                .precio(new BigDecimal("185.50"))
                .stock(10)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe crear un producto exitosamente cuando el SKU no está duplicado")
    void crearProducto_Exito() {
        when(productoRepository.existsByCodigoSku("FLT-IND-001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoEntity);

        ProductoResponseDTO response = productoService.crearProducto(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCodigoSku()).isEqualTo("FLT-IND-001");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar DuplicateSkuException al intentar crear un producto con SKU existente")
    void crearProducto_SkuDuplicado_LanzaExcepcion() {
        when(productoRepository.existsByCodigoSku("FLT-IND-001")).thenReturn(true);

        assertThatThrownBy(() -> productoService.crearProducto(requestDTO))
                .isInstanceOf(DuplicateSkuException.class)
                .hasMessageContaining("FLT-IND-001");

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe retornar ProductoResponseDTO al buscar por un ID existente")
    void obtenerPorId_Exito() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEntity));

        ProductoResponseDTO response = productoService.obtenerPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Filtro Industrial Hidráulico 25µm");
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al buscar por un ID inexistente")
    void obtenerPorId_NoEncontrado_LanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Debe cambiar el estado activo a false al desactivar un producto")
    void desactivarProducto_Exito() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEntity));

        productoService.desactivarProducto(1L);

        assertThat(productoEntity.getActivo()).isFalse();
        verify(productoRepository, times(1)).save(productoEntity);
    }
}