package com.wms.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 120, message = "El nombre no debe exceder los 120 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no debe exceder los 255 caracteres")
    private String descripcion;

    @NotBlank(message = "El código SKU es obligatorio")
    @Size(max = 50, message = "El SKU no debe exceder los 50 caracteres")
    private String codigoSku;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @NotNull(message = "El stock inicial es obligatorio")
    @PositiveOrZero(message = "El stock inicial no puede ser negativo")
    private Integer stock;

    private Long ubicacionId;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
}