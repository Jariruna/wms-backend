package com.wms.backend.service;

import com.wms.backend.dto.UbicacionRequestDTO;
import com.wms.backend.dto.UbicacionResponseDTO;

import java.util.List;

public interface UbicacionService {

    // --- MÉTODOS DE CREACIÓN Y BÚSQUEDA GENERAL ---
    UbicacionResponseDTO crear(UbicacionRequestDTO requestDTO);

    UbicacionResponseDTO obtenerPorId(Long id);

    UbicacionResponseDTO obtenerPorCodigo(String codigoUbicacion);

    List<UbicacionResponseDTO> obtenerTodas();

    List<UbicacionResponseDTO> obtenerActivas();

    // --- FILTROS ESPECÍFICOS DE ALMACÉN ---
    List<UbicacionResponseDTO> obtenerPorZona(String zona);

    List<UbicacionResponseDTO> obtenerPorEstadoOcupacion(Boolean ocupada);

    List<UbicacionResponseDTO> obtenerPorPasilloYRack(String pasillo, String rack);

    // --- ACTUALIZACIÓN Y CAMBIOS DE ESTADO ---
    UbicacionResponseDTO actualizar(Long id, UbicacionRequestDTO requestDTO);

    UbicacionResponseDTO cambiarEstadoOcupacion(Long id, Boolean ocupada);

    void cambiarEstadoActivo(Long id, boolean activa);

    // --- ELIMINACIÓN ---
    void eliminar(Long id);
}