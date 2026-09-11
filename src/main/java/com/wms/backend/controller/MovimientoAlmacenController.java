package com.wms.backend.controller;

import com.wms.backend.dto.MovimientoRequestDTO;
import com.wms.backend.dto.MovimientoResponseDTO;
import com.wms.backend.service.MovimientoAlmacenService;
import com.wms.backend.util.ExcelReportHelper;
import com.wms.backend.util.PdfReportHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movimientos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:4200"})
@RequiredArgsConstructor
@Tag(name = "Kardex / Movimientos", description = "Endpoints para la gestión y registro de movimientos de inventario en almacén")
public class MovimientoAlmacenController {

    private final MovimientoAlmacenService movimientoService;
    private final ExcelReportHelper excelReportHelper;
    private final PdfReportHelper pdfReportHelper;

    @Operation(summary = "Registrar un movimiento de inventario", description = "Registra una entrada, salida, reubicación o ajuste de stock para un producto asociándolo a una ubicación física.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Producto o ubicación no encontrada")
    })
    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> registrarMovimiento(@Valid @RequestBody MovimientoRequestDTO requestDTO) {
        MovimientoResponseDTO nuevoMovimiento = movimientoService.registrarMovimiento(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
    }

    @Operation(summary = "Obtener el Kardex completo", description = "Devuelve el historial general de todos los movimientos de inventario registrados en el almacén.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial general obtenido exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerTodosLosMovimientos() {
        List<MovimientoResponseDTO> movimientos = movimientoService.obtenerTodosLosMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    @Operation(summary = "Obtener el Kardex de un producto", description = "Devuelve el historial de movimientos de inventario de un producto específico ordenados cronológicamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerKardexPorProducto(@PathVariable Long productoId) {
        List<MovimientoResponseDTO> kardex = movimientoService.obtenerKardexPorProducto(productoId);
        return ResponseEntity.ok(kardex);
    }

    @Operation(summary = "Obtener el Kardex por ubicación física", description = "Devuelve el historial de movimientos registrados dentro de un rack o posición específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de ubicación obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ubicación no encontrada")
    })
    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerKardexPorUbicacion(@PathVariable Long ubicacionId) {
        List<MovimientoResponseDTO> kardex = movimientoService.obtenerKardexPorUbicacion(ubicacionId);
        return ResponseEntity.ok(kardex);
    }

    @Operation(summary = "Exportar Kardex de producto a Excel", description = "Genera un archivo Excel (.xlsx) con el historial de movimientos de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo Excel generado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/producto/{productoId}/export/excel")
    public ResponseEntity<byte[]> exportarKardexExcel(@PathVariable Long productoId) {
        List<MovimientoResponseDTO> movimientos = movimientoService.obtenerKardexPorProducto(productoId);
        String codigoIdentificador = "PROD-" + productoId;
        byte[] excelBytes = excelReportHelper.generarReporteKardexExcel(movimientos, codigoIdentificador);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "Kardex_Producto_" + productoId + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @Operation(summary = "Exportar Kardex de producto a PDF", description = "Genera un documento PDF con la tabla estilizada del Kardex de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo PDF generado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/producto/{productoId}/export/pdf")
    public ResponseEntity<byte[]> exportarKardexPdf(@PathVariable Long productoId) {
        List<MovimientoResponseDTO> movimientos = movimientoService.obtenerKardexPorProducto(productoId);
        String codigoIdentificador = "PROD-" + productoId;
        byte[] pdfBytes = pdfReportHelper.generarReporteKardexPdf(movimientos, codigoIdentificador);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "Kardex_Producto_" + productoId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}