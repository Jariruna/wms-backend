package com.wms.backend.service;

import com.wms.backend.dto.UbicacionRequestDTO;
import com.wms.backend.dto.UbicacionResponseDTO;

import java.util.List;

public interface UbicacionService {

    UbicacionResponseDTO crearUbicacion(UbicacionRequestDTO requestDTO);

    UbicacionResponseDTO obtenerPorId(Long id);

    UbicacionResponseDTO obtenerPorCodigo(String codigoUbicacion);

    List<UbicacionResponseDTO> obtenerTodas();

    List<UbicacionResponseDTO> obtenerPorEstadoOcupacion(Boolean ocupada);

    List<UbicacionResponseDTO> obtenerPorPasilloYRack(String pasillo, String rack);

    UbicacionResponseDTO actualizarUbicacion(Long id, UbicacionRequestDTO requestDTO);

    UbicacionResponseDTO cambiarEstadoOcupacion(Long id, Boolean ocupada);

    void eliminarUbicacion(Long id);
}