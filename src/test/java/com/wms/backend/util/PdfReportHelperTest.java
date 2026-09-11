package com.wms.backend.util;

import com.lowagie.text.pdf.PdfReader;
import com.wms.backend.domain.TipoMovimiento;
import com.wms.backend.dto.MovimientoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfReportHelperTest {

    private PdfReportHelper pdfReportHelper;

    @BeforeEach
    void setUp() {
        pdfReportHelper = new PdfReportHelper();
    }

    @Test
    @DisplayName("Debe generar un arreglo de bytes de PDF válido con registros")
    void generarReporteKardexPdf_ConDatos_Exito() throws IOException {
        // Arrange
        String codigoProducto = "PROD-100";
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 30, 10, 30, 0);

        MovimientoResponseDTO mov = MovimientoResponseDTO.builder()
                .id(1L)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(50)
                .stockAnterior(10)
                .stockResultante(60)
                .fechaMovimiento(fecha)
                .usuario("jninaco")
                .build();

        List<MovimientoResponseDTO> movimientos = List.of(mov);

        // Act
        byte[] pdfBytes = pdfReportHelper.generarReporteKardexPdf(movimientos, codigoProducto);

        // Assert
        assertThat(pdfBytes).isNotNull().isNotEmpty();

        // Validar que OpenPDF sea capaz de leer la estructura interna del PDF generado
        PdfReader reader = new PdfReader(pdfBytes);
        assertThat(reader.getNumberOfPages()).isEqualTo(1);
        reader.close();
    }

    @Test
    @DisplayName("Debe generar la estructura base de PDF cuando la lista de movimientos está vacía")
    void generarReporteKardexPdf_ListaVacia_Exito() throws IOException {
        // Arrange
        String codigoProducto = "PROD-200";

        // Act
        byte[] pdfBytes = pdfReportHelper.generarReporteKardexPdf(Collections.emptyList(), codigoProducto);

        // Assert
        assertThat(pdfBytes).isNotNull().isNotEmpty();

        PdfReader reader = new PdfReader(pdfBytes);
        assertThat(reader.getNumberOfPages()).isEqualTo(1);
        reader.close();
    }
}