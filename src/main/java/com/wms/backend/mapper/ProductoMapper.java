package com.wms.backend.mapper;

import com.wms.backend.domain.Producto;
import com.wms.backend.domain.UbicacionAlmacen;
import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoMapper {

    private final UbicacionMapper ubicacionMapper;

    public Producto toEntity(ProductoRequestDTO dto, UbicacionAlmacen ubicacion) {
        if (dto == null) {
            return null;
        }
        return Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .codigoSku(dto.getCodigoSku())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .ubicacion(ubicacion)
                .activo(true)
                .build();
    }

    public ProductoResponseDTO toResponseDTO(Producto entity) {
        if (entity == null) {
            return null;
        }
        return ProductoResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .codigoSku(entity.getCodigoSku())
                .precio(entity.getPrecio())
                .stock(entity.getStock())
                .activo(entity.getActivo())
                .ubicacion(ubicacionMapper.toDTO(entity.getUbicacion()))
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public void updateEntityFromDTO(ProductoRequestDTO dto, Producto entity, UbicacionAlmacen ubicacion) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCodigoSku(dto.getCodigoSku());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        entity.setUbicacion(ubicacion);
    }
}