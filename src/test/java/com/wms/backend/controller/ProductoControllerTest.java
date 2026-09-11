package com.wms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.dto.ProductoRequestDTO;
import com.wms.backend.dto.ProductoResponseDTO;
import com.wms.backend.exception.DuplicateSkuException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    private ProductoRequestDTO requestDTO;
    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = ProductoRequestDTO.builder()
                .nombre("Filtro Industrial Hidráulico 25µm")
                .descripcion("Filtro de alta eficiencia para maquinaria pesada")
                .codigoSku("FLT-IND-001")
                .precio(new BigDecimal("185.50"))
                .stock(10)
                .build();

        responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Filtro Industrial Hidráulico 25µm")
                .descripcion("Filtro de alta eficiencia para maquinaria pesada")
                .codigoSku("FLT-IND-001")
                .precio(new BigDecimal("185.50"))
                .stock(10)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/productos - Debe retornar 201 CREATED al crear producto válido")
    void crearProducto_Exito() throws Exception {
        when(productoService.crearProducto(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigoSku").value("FLT-IND-001"))
                .andExpect(jsonPath("$.nombre").value("Filtro Industrial Hidráulico 25µm"));
    }

    @Test
    @DisplayName("POST /api/v1/productos - Debe retornar 400 BAD REQUEST si los datos de entrada son inválidos")
    void crearProducto_ValidacionFallida_Retorna400() throws Exception {
        ProductoRequestDTO dtoInvalido = ProductoRequestDTO.builder()
                .nombre("") // Nombre vacío provoca fallo de validación @NotBlank
                .codigoSku("")
                .precio(new BigDecimal("-10.00")) // Precio negativo invalida @DecimalMin
                .build();

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.codigoSku").exists())
                .andExpect(jsonPath("$.errors.precio").exists());
    }

    @Test
    @DisplayName("POST /api/v1/productos - Debe retornar 409 CONFLICT si el SKU ya existe")
    void crearProducto_SkuDuplicado_Retorna409() throws Exception {
        when(productoService.crearProducto(any(ProductoRequestDTO.class)))
                .thenThrow(new DuplicateSkuException("Ya existe un producto registrado con el SKU: FLT-IND-001"));

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflicto de SKU"));
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id} - Debe retornar 200 OK con el producto encontrado")
    void obtenerPorId_Exito() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigoSku").value("FLT-IND-001"));
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id} - Debe retornar 404 NOT FOUND si el ID no existe")
    void obtenerPorId_NoEncontrado_Retorna404() throws Exception {
        when(productoService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID: 99"));

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/productos/{id} - Debe retornar 244 NO CONTENT al desactivar")
    void desactivarProducto_Exito() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());
    }
}