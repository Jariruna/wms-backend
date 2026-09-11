package com.wms.backend.mapper;

import com.wms.backend.domain.MovimientoAlmacen;
import com.wms.backend.domain.UbicacionAlmacen; // Cambiado desde Ubicacion
import com.wms.backend.dto.MovimientoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {

    public MovimientoResponseDTO toResponseDTO(MovimientoAlmacen movimiento) {
        if (movimiento == null) {
            return null;
        }

        MovimientoResponseDTO.MovimientoResponseDTOBuilder builder = MovimientoResponseDTO.builder()
                .id(movimiento.getId())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .cantidad(movimiento.getCantidad())
                .stockAnterior(movimiento.getStockAnterior())
                .stockResultante(movimiento.getStockResultante())
                .motivo(movimiento.getMotivo())
                .usuario(movimiento.getUsuario())
                .fechaMovimiento(movimiento.getFechaMovimiento());

        if (movimiento.getProducto() != null) {
            builder.productoId(movimiento.getProducto().getId())
                    .productoCodigoSku(movimiento.getProducto().getCodigoSku())
                    .productoNombre(movimiento.getProducto().getNombre());
        }

        if (movimiento.getUbicacion() != null) {
            UbicacionAlmacen ubicacion = movimiento.getUbicacion(); // Tipo corregido a UbicacionAlmacen

            builder.ubicacionId(ubicacion.getId())
                    .ubicacionCodigo(ubicacion.getCodigoUbicacion()) // Cambiado getCodigo() por getCodigoUbicacion()
                    .ubicacionPasillo(ubicacion.getPasillo())       // Mapeo a pasillo de UbicacionAlmacen
                    .ubicacionRack(ubicacion.getRack());            // Mapeo a rack de UbicacionAlmacen
        }

        return builder.build();
    }
}