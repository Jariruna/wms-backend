package com.wms.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ubicaciones_almacen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_ubicacion", nullable = false, unique = true, length = 50)
    private String codigoUbicacion;

    @Column(nullable = false, length = 50)
    private String zona;

    @Column(nullable = false, length = 20)
    private String pasillo;

    @Column(nullable = false, length = 20)
    private String rack;

    @Column(nullable = false, length = 20)
    private String nivel;

    @Column(length = 20)
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

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }

        // Si zona viene nula por un mal mapeo del Builder/DTO, asigna un valor por defecto
        if (this.zona == null || this.zona.isBlank()) {
            this.zona = "GENERAL";
        }

        if (this.ocupada == null) this.ocupada = false;
        if (this.activa == null) this.activa = true;
        if (this.capacidadMaxima == null) this.capacidadMaxima = 100;

        if (this.codigoUbicacion == null && pasillo != null && rack != null && nivel != null) {
            this.codigoUbicacion = String.format("%s-P%s-R%s-N%s", zona, pasillo, rack, nivel);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}