package com.wms.backend.service.impl;

import com.wms.backend.domain.Producto;
import com.wms.backend.domain.UbicacionAlmacen;
import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;
import com.wms.backend.exception.DuplicateSkuException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.mapper.ProductoMapper;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {
        if (productoRepository.existsByCodigoSku(requestDTO.getCodigoSku())) {
            throw new DuplicateSkuException("Ya existe un producto registrado con el SKU: " + requestDTO.getCodigoSku());
        }

        UbicacionAlmacen ubicacion = obtenerUbicacionSiExiste(requestDTO.getUbicacionId());
        Producto producto = productoMapper.toEntity(requestDTO, ubicacion);
        Producto productoGuardado = productoRepository.save(producto);
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
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponseDTO)
                .toList();
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
}