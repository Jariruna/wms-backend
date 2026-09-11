package com.wms.backend.util;

import com.wms.backend.domain.TipoMovimiento;
import com.wms.backend.dto.MovimientoResponseDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelReportHelperTest {

    private ExcelReportHelper excelReportHelper;

    @BeforeEach
    void setUp() {
        excelReportHelper = new ExcelReportHelper();
    }

    @Test
    @DisplayName("Debe generar un reporte de Excel válido con datos correctamente formateados")
    void generarReporteKardexExcel_ConDatos_Exito() throws IOException {
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
        byte[] excelBytes = excelReportHelper.generarReporteKardexExcel(movimientos, codigoProducto);

        // Assert
        assertThat(excelBytes).isNotNull().isNotEmpty();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheet("Kardex - " + codigoProducto);
            assertThat(sheet).isNotNull();
            assertThat(sheet.getLastRowNum()).isEqualTo(1);

            // Validar Cabecera (Fila 0)
            Row headerRow = sheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("ID Movimiento");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("Tipo");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("Cantidad");
            assertThat(headerRow.getCell(3).getStringCellValue()).isEqualTo("Stock Previo");
            assertThat(headerRow.getCell(4).getStringCellValue()).isEqualTo("Stock Resultante");
            assertThat(headerRow.getCell(5).getStringCellValue()).isEqualTo("Fecha/Hora");
            assertThat(headerRow.getCell(6).getStringCellValue()).isEqualTo("Usuario");

            // Validar Datos (Fila 1)
            Row dataRow = sheet.getRow(1);
            assertThat(dataRow.getCell(0).getNumericCellValue()).isEqualTo(1.0);
            assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("ENTRADA");
            assertThat(dataRow.getCell(2).getNumericCellValue()).isEqualTo(50.0);
            assertThat(dataRow.getCell(3).getNumericCellValue()).isEqualTo(10.0);
            assertThat(dataRow.getCell(4).getNumericCellValue()).isEqualTo(60.0);
            assertThat(dataRow.getCell(5).getStringCellValue()).isEqualTo("2026-03-30 10:30:00");
            assertThat(dataRow.getCell(6).getStringCellValue()).isEqualTo("jninaco");
        }
    }

    @Test
    @DisplayName("Debe generar la cabecera del Excel correctamente cuando la lista de movimientos está vacía")
    void generarReporteKardexExcel_ListaVacia_Exito() throws IOException {
        // Arrange
        String codigoProducto = "PROD-200";

        // Act
        byte[] excelBytes = excelReportHelper.generarReporteKardexExcel(Collections.emptyList(), codigoProducto);

        // Assert
        assertThat(excelBytes).isNotNull().isNotEmpty();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheet("Kardex - " + codigoProducto);
            assertThat(sheet).isNotNull();
            assertThat(sheet.getLastRowNum()).isEqualTo(0);
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("ID Movimiento");
        }
    }
}