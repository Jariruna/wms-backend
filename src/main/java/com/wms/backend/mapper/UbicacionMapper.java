package com.wms.backend.mapper;

import com.wms.backend.domain.UbicacionAlmacen;
import com.wms.backend.dto.UbicacionRequestDTO;
import com.wms.backend.dto.UbicacionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UbicacionMapper {

    public UbicacionAlmacen toEntity(UbicacionRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return UbicacionAlmacen.builder()
                .codigoUbicacion(dto.getCodigoUbicacion())
                .zona(dto.getZona()) // <-- AÑADIDO
                .pasillo(dto.getPasillo())
                .rack(dto.getRack())
                .nivel(dto.getNivel())
                .posicion(dto.getPosicion())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .ocupada(false) // Valor inicial por defecto al crear
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .build();
    }

    public UbicacionResponseDTO toDTO(UbicacionAlmacen entity) {
        if (entity == null) {
            return null;
        }

        return UbicacionResponseDTO.builder()
                .id(entity.getId())
                .codigoUbicacion(entity.getCodigoUbicacion())
                .zona(entity.getZona()) // <-- AÑADIDO
                .pasillo(entity.getPasillo())
                .rack(entity.getRack())
                .nivel(entity.getNivel())
                .posicion(entity.getPosicion())
                .capacidadMaxima(entity.getCapacidadMaxima())
                .ocupada(entity.getOcupada())
                .activa(entity.getActiva())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    public void updateEntityFromDTO(UbicacionRequestDTO dto, UbicacionAlmacen entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setCodigoUbicacion(dto.getCodigoUbicacion());
        entity.setZona(dto.getZona()); // <-- AÑADIDO
        entity.setPasillo(dto.getPasillo());
        entity.setRack(dto.getRack());
        entity.setNivel(dto.getNivel());
        entity.setPosicion(dto.getPosicion());
        if (dto.getCapacidadMaxima() != null) {
            entity.setCapacidadMaxima(dto.getCapacidadMaxima());
        }
        if (dto.getActiva() != null) {
            entity.setActiva(dto.getActiva());
        }
    }
}