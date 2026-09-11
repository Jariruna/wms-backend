package com.wms.backend.util;

import com.wms.backend.dto.MovimientoResponseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelReportHelper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] generarReporteKardexExcel(List<MovimientoResponseDTO> movimientos, String codigoProducto) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Kardex - " + codigoProducto);

            // Estilos del libro
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dataStyle = crearEstiloCeldaDatos(workbook);

            // Encabezados de la tabla
            String[] columnas = {"ID Movimiento", "Tipo", "Cantidad", "Stock Previo", "Stock Resultante", "Fecha/Hora", "Usuario"};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenado de filas
            int rowIdx = 1;
            for (MovimientoResponseDTO dto : movimientos) {
                Row row = sheet.createRow(rowIdx++);

                crearCelda(row, 0, dto.getId(), dataStyle);
                crearCelda(row, 1, dto.getTipoMovimiento().name(), dataStyle);
                crearCelda(row, 2, dto.getCantidad(), dataStyle);
                crearCelda(row, 3, dto.getStockAnterior(), dataStyle);
                crearCelda(row, 4, dto.getStockResultante(), dataStyle);
                crearCelda(row, 5, dto.getFechaMovimiento().format(DATE_FORMATTER), dataStyle);
                crearCelda(row, 6, dto.getUsuario() != null ? dto.getUsuario() : "N/A", dataStyle);
            }

            // Autoajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el reporte de Excel para el Kardex: " + e.getMessage(), e);
        }
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        aplicarBordes(style);
        return style;
    }

    private CellStyle crearEstiloCeldaDatos(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        aplicarBordes(style);
        return style;
    }

    private void aplicarBordes(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }

    private void crearCelda(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }
}