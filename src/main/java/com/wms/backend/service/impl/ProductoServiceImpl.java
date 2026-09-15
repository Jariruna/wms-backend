package com.wms.backend.service.impl;

import com.wms.backend.domain.*;
import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;
import com.wms.backend.dto.StockUbicacionDTO;
import com.wms.backend.exception.DuplicateSkuException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.mapper.ProductoMapper;
import com.wms.backend.repository.MovimientoAlmacenRepository;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.StockUbicacionRepository;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final MovimientoAlmacenRepository movimientoRepository;
    private final ProductoMapper productoMapper;
    private final StockUbicacionRepository stockUbicacionRepository;


    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {
        if (productoRepository.existsByCodigoSku(requestDTO.getCodigoSku())) {
            throw new DuplicateSkuException("Ya existe un producto registrado con el SKU: " + requestDTO.getCodigoSku());
        }

        UbicacionAlmacen ubicacion = obtenerUbicacionSiExiste(requestDTO.getUbicacionId());
        Producto producto = productoMapper.toEntity(requestDTO, ubicacion);
        Producto productoGuardado = productoRepository.save(producto);

        if (productoGuardado.getStock() != null && productoGuardado.getStock() > 0 && ubicacion != null) {
            MovimientoAlmacen movimientoInicial = new MovimientoAlmacen();
            movimientoInicial.setProducto(productoGuardado);
            movimientoInicial.setUbicacion(ubicacion);
            movimientoInicial.setTipoMovimiento(TipoMovimiento.ENTRADA); // Ajusta según tu enum (ej. "INGRESO" o TipoMovimiento.ENTRADA)
            movimientoInicial.setCantidad(productoGuardado.getStock());
            movimientoInicial.setStockAnterior(0);
            movimientoInicial.setStockResultante(productoGuardado.getStock());
            movimientoInicial.setMotivo("Stock inicial de apertura");
            movimientoInicial.setUsuario("Sistema");
            movimientoInicial.setFechaMovimiento(LocalDateTime.now());

            movimientoRepository.save(movimientoInicial);

            StockUbicacion stockUbicacion = new StockUbicacion();
            stockUbicacion.setProducto(productoGuardado);
            stockUbicacion.setUbicacion(ubicacion);
            stockUbicacion.setCantidad(productoGuardado.getStock());
            stockUbicacionRepository.save(stockUbicacion);


        }

        return productoMapper.toResponseDTO(productoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return productoMapper.toResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorSku(String codigoSku) {
        Producto producto = productoRepository.findByCodigoSku(codigoSku)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU: " + codigoSku));
        return productoMapper.toResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(producto -> {
                    ProductoResponseDTO dto = productoMapper.toResponseDTO(producto);
                    cargarUbicacionesDetalle(producto.getId(), dto);
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(producto -> {
                    ProductoResponseDTO dto = productoMapper.toResponseDTO(producto);
                    cargarUbicacionesDetalle(producto.getId(), dto);
                    return dto;
                })
                .toList();
    }

    // Método auxiliar para poblar el desglose que requiere el frontend
    private void cargarUbicacionesDetalle(Long productoId, ProductoResponseDTO dto) {
        List<StockUbicacion> stocks = stockUbicacionRepository.findByProductoId(productoId);

        List<StockUbicacionDTO> detalle = stocks.stream().map(su ->
                StockUbicacionDTO.builder()
                        .id(su.getId())
                        .productoId(su.getProducto().getId())
                        .productoCodigoSku(su.getProducto().getCodigoSku())
                        .productoNombre(su.getProducto().getNombre())
                        .ubicacionId(su.getUbicacion().getId())
                        .ubicacionCodigo(su.getUbicacion().getCodigoUbicacion())
                        .zona(su.getUbicacion().getZona())
                        .stock(su.getCantidad())
                        .build()
        ).toList();

        dto.setUbicacionesDetalle(detalle);
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO requestDTO) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!productoExistente.getCodigoSku().equals(requestDTO.getCodigoSku())
                && productoRepository.existsByCodigoSku(requestDTO.getCodigoSku())) {
            throw new DuplicateSkuException("El nuevo SKU especificado ya está en uso: " + requestDTO.getCodigoSku());
        }

        UbicacionAlmacen ubicacion = obtenerUbicacionSiExiste(requestDTO.getUbicacionId());
        productoMapper.updateEntityFromDTO(requestDTO, productoExistente, ubicacion);
        Producto productoActualizado = productoRepository.save(productoExistente);
        return productoMapper.toResponseDTO(productoActualizado);
    }

    @Override
    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private UbicacionAlmacen obtenerUbicacionSiExiste(Long ubicacionId) {
        if (ubicacionId == null) {
            return null;
        }
        return ubicacionRepository.findById(ubicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + ubicacionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosStockMinimo() {
        return productoRepository.findProductosConStockMinimo().stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

}