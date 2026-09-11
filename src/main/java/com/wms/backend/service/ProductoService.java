package com.wms.backend.service;

import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO);

    ProductoResponseDTO obtenerPorId(Long id);

    ProductoResponseDTO obtenerPorSku(String codigoSku);

    List<ProductoResponseDTO> listarActivos();

    List<ProductoResponseDTO> listarTodos();

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO requestDTO);

    void desactivarProducto(Long id);
}