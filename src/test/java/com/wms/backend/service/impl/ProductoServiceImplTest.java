package com.wms.backend.service.impl;

import com.wms.backend.domain.*;
import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;
import com.wms.backend.exception.DuplicateSkuException;
import com.wms.backend.mapper.ProductoMapper;
import com.wms.backend.repository.MovimientoAlmacenRepository;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.StockUbicacionRepository;
import com.wms.backend.repository.UbicacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UbicacionRepository ubicacionRepository;

    @Mock
    private MovimientoAlmacenRepository movimientoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @Mock
    private StockUbicacionRepository stockUbicacionRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void crearProducto_exitosoConStockYUbicacion() {
        ProductoRequestDTO requestDTO = new ProductoRequestDTO();
        requestDTO.setCodigoSku("SKU-TEST-01");
        requestDTO.setStock(20);
        requestDTO.setUbicacionId(1L);

        UbicacionAlmacen ubicacion = new UbicacionAlmacen();
        ubicacion.setId(1L);

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setCodigoSku("SKU-TEST-01");
        producto.setStock(20);

        ProductoResponseDTO responseDTO = new ProductoResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setCodigoSku("SKU-TEST-01");

        when(productoRepository.existsByCodigoSku("SKU-TEST-01")).thenReturn(false);
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacion));
        when(productoMapper.toEntity(requestDTO, ubicacion)).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertNotNull(resultado);
        assertEquals("SKU-TEST-01", resultado.getCodigoSku());

        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoAlmacen.class));
        verify(stockUbicacionRepository, times(1)).save(any(StockUbicacion.class));
    }

    @Test
    void crearProducto_fallaPorSkuDuplicado() {
        ProductoRequestDTO requestDTO = new ProductoRequestDTO();
        requestDTO.setCodigoSku("SKU-DUP");

        when(productoRepository.existsByCodigoSku("SKU-DUP")).thenReturn(true);

        assertThrows(DuplicateSkuException.class, () -> {
            productoService.crearProducto(requestDTO);
        });

        verify(productoRepository, never()).save(any(Producto.class));
        verify(movimientoRepository, never()).save(any(MovimientoAlmacen.class));
        verify(stockUbicacionRepository, never()).save(any(StockUbicacion.class));
    }
}