package com.wms.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ubicacion_almacen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_ubicacion", nullable = false, unique = true, length = 30)
    private String codigoUbicacion;

    @Column(nullable = false, length = 10)
    private String pasillo;

    @Column(nullable = false, length = 10)
    private String rack;

    @Column(nullable = false, length = 10)
    private String nivel;

    @Column(length = 10)
    private String posicion;

    @Column(name = "capacidad_maxima", nullable = false)
    @Builder.Default
    private Integer capacidadMaxima = 100;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ocupada = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.codigoUbicacion == null && pasillo != null && rack != null && nivel != null) {
            this.codigoUbicacion = String.format("P%s-R%s-N%s", pasillo, rack, nivel);
        }
    }
}