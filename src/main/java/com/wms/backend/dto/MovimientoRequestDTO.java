package com.wms.backend.dto;

import com.wms.backend.domain.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "El ID de la ubicación es obligatorio")
    private Long ubicacionId; // Actúa como ubicación Destino para Reubicación o Principal para Entradas/Salidas

    private Long ubicacionOrigenId; // Opcional en Bean Validation (se valida en servicio cuando tipoMovimiento == REUBICACION)

    @NotNull(message = "El tipo de movimiento es obligatorio (ENTRADA, SALIDA, REUBICACION, AJUSTE_INVENTARIO)")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser de al menos 1 unidad")
    private Integer cantidad;

    @Size(max = 255, message = "El motivo no puede exceder los 255 caracteres")
    private String motivo;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 100, message = "El usuario no puede exceder los 100 caracteres")
    private String usuario;
}