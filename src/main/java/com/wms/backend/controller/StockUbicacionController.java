package com.wms.backend.controller;

import com.wms.backend.dto.StockUbicacionResponseDTO;
import com.wms.backend.service.StockUbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-ubicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockUbicacionController {

    private final StockUbicacionService stockUbicacionService; // Debe ser private final

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
}