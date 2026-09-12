package com.wms.backend.controller;

import com.wms.backend.dto.StockUbicacionRequestDTO;
import com.wms.backend.dto.StockUbicacionResponseDTO;
import com.wms.backend.service.StockUbicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-ubicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockUbicacionController {

    private final StockUbicacionService stockUbicacionService;

    @PostMapping
    public ResponseEntity<StockUbicacionResponseDTO> guardarOActualizarStock(@Valid @RequestBody StockUbicacionRequestDTO dto) {
        StockUbicacionResponseDTO respuesta = stockUbicacionService.guardarOActualizarStock(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<StockUbicacionResponseDTO>> obtenerTodoElStock() {
        List<StockUbicacionResponseDTO> stockList = stockUbicacionService.obtenerTodoElStock();
        return ResponseEntity.ok(stockList);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<StockUbicacionResponseDTO>> obtenerStockPorProducto(@PathVariable Long productoId) {
        List<StockUbicacionResponseDTO> stockProducto = stockUbicacionService.obtenerStockPorProducto(productoId);
        return ResponseEntity.ok(stockProducto);
    }

    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<StockUbicacionResponseDTO>> obtenerStockPorUbicacion(@PathVariable Long ubicacionId) {
        List<StockUbicacionResponseDTO> stockUbicacion = stockUbicacionService.obtenerStockPorUbicacion(ubicacionId);
        return ResponseEntity.ok(stockUbicacion);
    }

    @GetMapping("/producto/{productoId}/ubicacion/{ubicacionId}")
    public ResponseEntity<StockUbicacionResponseDTO> obtenerStockEspecifico(
            @PathVariable Long productoId,
            @PathVariable Long ubicacionId) {
        StockUbicacionResponseDTO stockEspecifico = stockUbicacionService.obtenerStockEspecifico(productoId, ubicacionId);
        return ResponseEntity.ok(stockEspecifico);
    }
}