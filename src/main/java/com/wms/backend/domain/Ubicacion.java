package com.wms.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ubicaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 50)
    private String zona;

    @Column(length = 20)
    private String pasillo;

    @Column(length = 20)
    private String rack;

    @Column(length = 20)
    private String nivel;

    @Column(name = "capacidad_maxima", nullable = false)
    @Builder.Default
    private Integer capacidadMaxima = 100;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ocupado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.ocupado == null) this.ocupado = false;
        if (this.activo == null) this.activo = true;
        if (this.capacidadMaxima == null) this.capacidadMaxima = 100;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}