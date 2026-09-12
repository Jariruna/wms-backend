package com.wms.backend.service.impl;

import com.wms.backend.domain.Producto;
import com.wms.backend.domain.StockUbicacion;
import com.wms.backend.domain.UbicacionAlmacen;
import com.wms.backend.dto.StockUbicacionRequestDTO;
import com.wms.backend.dto.StockUbicacionResponseDTO;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.StockUbicacionRepository;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.StockUbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockUbicacionServiceImpl implements StockUbicacionService {

    private final StockUbicacionRepository stockUbicacionRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;

    @Override
    @Transactional
    public StockUbicacionResponseDTO guardarOActualizarStock(StockUbicacionRequestDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + dto.getProductoId()));

        UbicacionAlmacen ubicacion = ubicacionRepository.findById(dto.getUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con el ID: " + dto.getUbicacionId()));

        StockUbicacion stock = stockUbicacionRepository
                .findByProductoIdAndUbicacionId(dto.getProductoId(), dto.getUbicacionId())
                .orElse(StockUbicacion.builder()
                        .producto(producto)
                        .ubicacion(ubicacion)
                        .cantidad(0)
                        .build());

        stock.setCantidad(dto.getCantidad());
        StockUbicacion guardado = stockUbicacionRepository.save(stock);

        return mapToDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockUbicacionResponseDTO> obtenerTodoElStock() {
        return stockUbicacionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockUbicacionResponseDTO> obtenerStockPorProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no encontrado con el ID: " + productoId);
        }

        return stockUbicacionRepository.findByProductoId(productoId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockUbicacionResponseDTO> obtenerStockPorUbicacion(Long ubicacionId) {
        if (!ubicacionRepository.existsById(ubicacionId)) {
            throw new ResourceNotFoundException("Ubicación no encontrada con el ID: " + ubicacionId);
        }

        return stockUbicacionRepository.findByUbicacionId(ubicacionId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockUbicacionResponseDTO obtenerStockEspecifico(Long productoId, Long ubicacionId) {
        return stockUbicacionRepository.findByProductoIdAndUbicacionId(productoId, ubicacionId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No existe registro de stock para el Producto ID %d en la Ubicación ID %d",
                                productoId, ubicacionId)
                ));
    }

    private StockUbicacionResponseDTO mapToDTO(StockUbicacion stockUbicacion) {
        StockUbicacionResponseDTO.StockUbicacionResponseDTOBuilder builder = StockUbicacionResponseDTO.builder()
                .id(stockUbicacion.getId())
                .cantidad(stockUbicacion.getCantidad())
                .fechaActualizacion(stockUbicacion.getFechaActualizacion());

        if (stockUbicacion.getProducto() != null) {
            builder.productoId(stockUbicacion.getProducto().getId())
                    .productoCodigoSku(stockUbicacion.getProducto().getCodigoSku())
                    .productoNombre(stockUbicacion.getProducto().getNombre());
        }

        if (stockUbicacion.getUbicacion() != null) {
            builder.ubicacionId(stockUbicacion.getUbicacion().getId())
                    .ubicacionCodigo(stockUbicacion.getUbicacion().getCodigoUbicacion())
                    .ubicacionZona(stockUbicacion.getUbicacion().getPosicion())
                    .ubicacionPasillo(stockUbicacion.getUbicacion().getPasillo())
                    .ubicacionRack(stockUbicacion.getUbicacion().getRack())
                    .ubicacionNivel(stockUbicacion.getUbicacion().getNivel());
        }

        return builder.build();
    }
}