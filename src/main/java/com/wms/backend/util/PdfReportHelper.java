package com.wms.backend.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.wms.backend.dto.MovimientoResponseDTO;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfReportHelper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] generarReporteKardexPdf(List<MovimientoResponseDTO> movimientos, String codigoProducto) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            // Encabezado del Documento
            Paragraph title = new Paragraph("Reporte de Kardex de Inventario", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Producto Código: " + codigoProducto, subTitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Configuración de la Tabla (7 columnas)
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.2f, 1.5f, 1.2f, 1.5f, 1.5f, 2.5f, 2.0f});

            // Headers
            String[] columnas = {"ID", "Tipo", "Cantidad", "Stk Previo", "Stk Result.", "Fecha/Hora", "Usuario"};
            Color navyBlue = new Color(0, 51, 102);

            for (String col : columnas) {
                PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
                cell.setBackgroundColor(navyBlue);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(6);
                table.addCell(cell);
            }

            // Filas de Datos
            for (MovimientoResponseDTO dto : movimientos) {
                agregarCelda(table, String.valueOf(dto.getId()), cellFont, Element.ALIGN_CENTER);
                agregarCelda(table, dto.getTipoMovimiento() != null ? dto.getTipoMovimiento().name() : "", cellFont, Element.ALIGN_CENTER);
                agregarCelda(table, String.valueOf(dto.getCantidad()), cellFont, Element.ALIGN_RIGHT);
                agregarCelda(table, String.valueOf(dto.getStockAnterior()), cellFont, Element.ALIGN_RIGHT);
                agregarCelda(table, String.valueOf(dto.getStockResultante()), cellFont, Element.ALIGN_RIGHT);
                agregarCelda(table, dto.getFechaMovimiento() != null ? dto.getFechaMovimiento().format(DATE_FORMATTER) : "", cellFont, Element.ALIGN_CENTER);
                agregarCelda(table, dto.getUsuario() != null ? dto.getUsuario() : "N/A", cellFont, Element.ALIGN_LEFT);
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el reporte PDF para el Kardex: " + e.getMessage(), e);
        }
    }

    private void agregarCelda(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }
}