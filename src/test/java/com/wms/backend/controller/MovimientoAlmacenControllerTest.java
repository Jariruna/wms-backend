package com.wms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.domain.TipoMovimiento;
import com.wms.backend.dto.MovimientoRequestDTO;
import com.wms.backend.dto.MovimientoResponseDTO;
import com.wms.backend.exception.InsufficientStockException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.security.CustomUserDetailsService;
import com.wms.backend.security.JwtUtils;
import com.wms.backend.service.MovimientoAlmacenService;
import com.wms.backend.util.ExcelReportHelper;
import com.wms.backend.util.PdfReportHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MovimientoAlmacenController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
class MovimientoAlmacenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovimientoAlmacenService movimientoService;

    @MockitoBean
    private ExcelReportHelper excelReportHelper;

    @MockitoBean
    private PdfReportHelper pdfReportHelper;

    // --- MOCKS DE SEGURIDAD REQUERIDOS ---
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private MovimientoRequestDTO requestDTO;
    private MovimientoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = MovimientoRequestDTO.builder()
                .productoId(1L)
                .ubicacionId(1L)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(10)
                .motivo("Ingreso por compra a proveedor")
                .usuario("jninaco")
                .build();

        responseDTO = MovimientoResponseDTO.builder()
                .id(100L)
                .productoId(1L)
                .ubicacionId(1L)
                .productoCodigoSku("FLT-001")
                .productoNombre("Filtro de Aceite")
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(10)
                .stockAnterior(5)
                .stockResultante(15)
                .motivo("Ingreso por compra a proveedor")
                .usuario("jninaco")
                .fechaMovimiento(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "jninaco", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/movimientos - Debe registrar movimiento y retornar 201 CREATED")
    void registrarMovimiento_Exito() throws Exception {
        when(movimientoService.registrarMovimiento(any(MovimientoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.productoId", is(1)))
                .andExpect(jsonPath("$.productoCodigoSku", is("FLT-001")))
                .andExpect(jsonPath("$.tipoMovimiento", is("ENTRADA")))
                .andExpect(jsonPath("$.cantidad", is(10)))
                .andExpect(jsonPath("$.stockAnterior", is(5)))
                .andExpect(jsonPath("$.stockResultante", is(15)))
                .andExpect(jsonPath("$.usuario", is("jninaco")));
    }

    @Test
    @DisplayName("POST /api/v1/movimientos - Debe retornar 400 BAD REQUEST cuando falta productoId")
    void registrarMovimiento_ValidacionFallida_FaltaProductoId() throws Exception {
        requestDTO.setProductoId(null);

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/movimientos - Debe retornar 400 BAD REQUEST por stock insuficiente")
    void registrarMovimiento_StockInsuficiente() throws Exception {
        requestDTO.setTipoMovimiento(TipoMovimiento.SALIDA);
        requestDTO.setCantidad(500);

        when(movimientoService.registrarMovimiento(any(MovimientoRequestDTO.class)))
                .thenThrow(new InsufficientStockException("Stock insuficiente para realizar la salida"));

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/movimientos/producto/{productoId} - Debe retornar 200 OK y la lista del Kardex")
    void obtenerKardexPorProducto_Exito() throws Exception {
        when(movimientoService.obtenerKardexPorProducto(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/movimientos/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].productoCodigoSku", is("FLT-001")))
                .andExpect(jsonPath("$[0].tipoMovimiento", is("ENTRADA")));
    }

    @Test
    @DisplayName("GET /api/v1/movimientos/producto/{productoId} - Debe retornar 404 NOT FOUND si el producto no existe")
    void obtenerKardexPorProducto_ProductoNoEncontrado() throws Exception {
        when(movimientoService.obtenerKardexPorProducto(99L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con el ID: 99"));

        mockMvc.perform(get("/api/v1/movimientos/producto/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/movimientos/producto/{productoId}/export/excel - Debe retornar 200 OK y descargar el archivo Excel")
    void exportarKardexExcel_Exito() throws Exception {
        byte[] mockExcel = new byte[]{1, 2, 3, 4};

        when(movimientoService.obtenerKardexPorProducto(1L)).thenReturn(List.of(responseDTO));
        when(excelReportHelper.generarReporteKardexExcel(any(), eq("PROD-1"))).thenReturn(mockExcel);

        mockMvc.perform(get("/api/v1/movimientos/producto/1/export/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"Kardex_Producto_1.xlsx\""))
                .andExpect(content().bytes(mockExcel));
    }

    @Test
    @DisplayName("GET /api/v1/movimientos/producto/{productoId}/export/pdf - Debe retornar 200 OK y retornar el archivo PDF")
    void exportarKardexPdf_Exito() throws Exception {
        byte[] mockPdf = new byte[]{5, 6, 7, 8};

        when(movimientoService.obtenerKardexPorProducto(1L)).thenReturn(List.of(responseDTO));
        when(pdfReportHelper.generarReporteKardexPdf(any(), eq("PROD-1"))).thenReturn(mockPdf);

        mockMvc.perform(get("/api/v1/movimientos/producto/1/export/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"inline\"; filename=\"Kardex_Producto_1.pdf\""))
                .andExpect(content().bytes(mockPdf));
    }
}