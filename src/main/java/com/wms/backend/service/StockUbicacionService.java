package com.wms.backend.service;

import com.wms.backend.dto.StockUbicacionRequestDTO;
import com.wms.backend.dto.StockUbicacionResponseDTO;
import java.util.List;

public interface StockUbicacionService {

    /**
     * Registra un nuevo saldo de stock o actualiza la cantidad existente
     * para la combinación Producto-Ubicación.
     */
    StockUbicacionResponseDTO guardarOActualizarStock(StockUbicacionRequestDTO dto);

    /**
     * Obtiene el listado completo de stock distribuido en todas las ubicaciones físicas del almacén.
     */
    List<StockUbicacionResponseDTO> obtenerTodoElStock();

    /**
     * Obtiene la distribución de stock en distintas ubicaciones para un producto específico.
     */
    List<StockUbicacionResponseDTO> obtenerStockPorProducto(Long productoId);

    /**
     * Obtiene todos los productos almacenados dentro de una ubicación específica (rack/nivel).
     */
    List<StockUbicacionResponseDTO> obtenerStockPorUbicacion(Long ubicacionId);

    /**
     * Obtiene el registro exacto de stock para un producto en una ubicación específica.
     */
    StockUbicacionResponseDTO obtenerStockEspecifico(Long productoId, Long ubicacionId);
}