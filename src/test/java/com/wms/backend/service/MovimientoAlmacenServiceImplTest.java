package com.wms.backend.service;

import com.wms.backend.domain.*;
import com.wms.backend.dto.MovimientoRequestDTO;
import com.wms.backend.dto.MovimientoResponseDTO;
import com.wms.backend.exception.InsufficientStockException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.repository.MovimientoAlmacenRepository;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.StockUbicacionRepository;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.impl.MovimientoAlmacenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoAlmacenServiceImplTest {

    @Mock
    private MovimientoAlmacenRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UbicacionRepository ubicacionRepository;

    @Mock
    private StockUbicacionRepository stockUbicacionRepository;

    @InjectMocks
    private MovimientoAlmacenServiceImpl movimientoService;

    private Producto productoEjemplo;
    private UbicacionAlmacen ubicacionEjemplo;
    private StockUbicacion stockEjemplo;

    @BeforeEach
    void setUp() {
        productoEjemplo = Producto.builder()
                .id(1L)
                .nombre("Filtro de Aceite")
                .codigoSku("FLT-001")
                .precio(new BigDecimal("150.00"))
                .stock(10)
                .build();

        ubicacionEjemplo = UbicacionAlmacen.builder()
                .id(1L)
                .codigoUbicacion("A1-R01-N01")
                .pasillo("A1")
                .rack("R01")
                .nivel("N01")
                .capacidadMaxima(100)
                .ocupada(false)
                .activa(true)
                .build();

        stockEjemplo = new StockUbicacion();
        stockEjemplo.setId(1L);
        stockEjemplo.setProducto(productoEjemplo);
        stockEjemplo.setUbicacion(ubicacionEjemplo);
        stockEjemplo.setCantidad(10);
    }

    @Test
    @DisplayName("Debe registrar un movimiento de ENTRADA exitosamente")
    void registrarMovimiento_EntradaExitosa() {
        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .productoId(1L)
                .ubicacionId(1L)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(5)
                .motivo("Ingreso de mercadería")
                .usuario("jninaco")
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacionEjemplo));
        when(stockUbicacionRepository.findByProductoIdAndUbicacionId(1L, 1L)).thenReturn(Optional.of(stockEjemplo));
        when(movimientoRepository.save(any(MovimientoAlmacen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoResponseDTO response = movimientoService.registrarMovimiento(request);

        assertNotNull(response);
        verify(productoRepository, times(1)).findById(1L);
        verify(movimientoRepository, times(1)).save(any(MovimientoAlmacen.class));
    }

    @Test
    @DisplayName("Debe registrar un movimiento de SALIDA exitosamente")
    void registrarMovimiento_SalidaExitosa() {
        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .productoId(1L)
                .ubicacionId(1L)
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .cantidad(4)
                .motivo("Despacho a taller")
                .usuario("jninaco")
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacionEjemplo));
        when(stockUbicacionRepository.findByProductoIdAndUbicacionId(1L, 1L)).thenReturn(Optional.of(stockEjemplo));
        when(movimientoRepository.save(any(MovimientoAlmacen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoResponseDTO response = movimientoService.registrarMovimiento(request);

        assertNotNull(response);
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar InsufficientStockException cuando la cantidad solicitada excede el stock")
    void registrarMovimiento_SalidaConStockInsuficiente_LanzaExcepcion() {
        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .productoId(1L)
                .ubicacionId(1L)
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .cantidad(50)
                .motivo("Despacho excesivo")
                .usuario("jninaco")
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacionEjemplo));

        // Uso de lenient() para evitar UnnecessaryStubbingException si la validación falla previamente
        lenient().when(stockUbicacionRepository.findByProductoIdAndUbicacionId(1L, 1L)).thenReturn(Optional.of(stockEjemplo));

        assertThrows(InsufficientStockException.class, () -> movimientoService.registrarMovimiento(request));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el producto no existe")
    void registrarMovimiento_ProductoNoEncontrado_LanzaExcepcion() {
        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .productoId(99L)
                .ubicacionId(1L)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(5)
                .usuario("jninaco")
                .build();

        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movimientoService.registrarMovimiento(request));
    }
}