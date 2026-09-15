package com.wms.backend.controller;

import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;
import com.wms.backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para la gestión del catálogo de productos e inventario base")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Crear un nuevo producto", description = "Registra un nuevo producto en la base de datos validando la unicidad del SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El código SKU ya se encuentra registrado")
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO requestDTO) {
        ProductoResponseDTO nuevoProducto = productoService.crearProducto(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna los detalles de un producto específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener producto por SKU", description = "Retorna los detalles de un producto buscando por su código SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/sku/{codigoSku}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorSku(@PathVariable String codigoSku) {
        return ResponseEntity.ok(productoService.obtenerPorSku(codigoSku));
    }

    @Operation(summary = "Listar productos activos", description = "Obtiene la lista de todos los productos habilitados en el sistema.")
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(productoService.listarActivos());
    }

    @Operation(summary = "Listar todos los productos", description = "Obtiene el catálogo completo de productos (activos e inactivos).")
    @GetMapping("/todos")
    public ResponseEntity<List<ProductoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con el nuevo código SKU")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO requestDTO) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, requestDTO));
    }

    @Operation(summary = "Desactivar producto (Borrado lógico)", description = "Cambia el estado del producto a inactivo (activo = false) sin eliminar el registro histórico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar productos con stock mínimo o crítico", description = "Obtiene la lista de productos cuyo stock actual es menor o igual al stock mínimo configurado.")
    @GetMapping("/stock-minimo")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductosStockMinimo() {
        return ResponseEntity.ok(productoService.listarProductosStockMinimo());
    }
}