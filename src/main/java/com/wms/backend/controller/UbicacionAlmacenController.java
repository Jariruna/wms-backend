package com.wms.backend.controller;

import com.wms.backend.dto.UbicacionRequestDTO;
import com.wms.backend.dto.UbicacionResponseDTO;
import com.wms.backend.service.UbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ubicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Ubicaciones", description = "Control y administración de posiciones físicas (Zona, Pasillo, Rack, Nivel)")
public class UbicacionAlmacenController {

    private final UbicacionService ubicacionService;

    @Operation(summary = "Crear ubicación", description = "Registra una nueva posición en el almacén.")
    @PostMapping
    public ResponseEntity<UbicacionResponseDTO> crearUbicacion(@Valid @RequestBody UbicacionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionService.crear(requestDTO));
    }

    @Operation(summary = "Obtener todas las ubicaciones activas")
    @GetMapping
    public ResponseEntity<List<UbicacionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(ubicacionService.obtenerTodas());
    }

    @Operation(summary = "Obtener por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UbicacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener por Código de ubicación")
    @GetMapping("/codigo/{codigoUbicacion}")
    public ResponseEntity<UbicacionResponseDTO> obtenerPorCodigo(@PathVariable String codigoUbicacion) {
        return ResponseEntity.ok(ubicacionService.obtenerPorCodigo(codigoUbicacion));
    }

    @Operation(summary = "Filtrar por Zona")
    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<UbicacionResponseDTO>> obtenerPorZona(@PathVariable String zona) {
        return ResponseEntity.ok(ubicacionService.obtenerPorZona(zona));
    }

    @Operation(summary = "Filtrar por Pasillo y Rack")
    @GetMapping("/pasillo/{pasillo}/rack/{rack}")
    public ResponseEntity<List<UbicacionResponseDTO>> obtenerPorPasilloYRack(
            @PathVariable String pasillo,
            @PathVariable String rack) {
        return ResponseEntity.ok(ubicacionService.obtenerPorPasilloYRack(pasillo, rack));
    }

    @Operation(summary = "Filtrar por estado de Ocupación")
    @GetMapping("/ocupada/{ocupada}")
    public ResponseEntity<List<UbicacionResponseDTO>> obtenerPorEstadoOcupacion(@PathVariable Boolean ocupada) {
        return ResponseEntity.ok(ubicacionService.obtenerPorEstadoOcupacion(ocupada));
    }

    @Operation(summary = "Actualizar ubicación")
    @PutMapping("/{id}")
    public ResponseEntity<UbicacionResponseDTO> actualizarUbicacion(
            @PathVariable Long id,
            @Valid @RequestBody UbicacionRequestDTO requestDTO) {
        return ResponseEntity.ok(ubicacionService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Cambiar estado de ocupación de una posición")
    @PatchMapping("/{id}/ocupacion")
    public ResponseEntity<UbicacionResponseDTO> cambiarEstadoOcupacion(
            @PathVariable Long id,
            @RequestParam Boolean ocupada) {
        return ResponseEntity.ok(ubicacionService.cambiarEstadoOcupacion(id, ocupada));
    }

    @Operation(summary = "Cambiar estado activo/inactivo (Soft Delete)")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoActivo(
            @PathVariable Long id,
            @RequestParam Boolean activa) {
        ubicacionService.cambiarEstadoActivo(id, activa);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminación física de ubicación")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUbicacion(@PathVariable Long id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}