package com.wms.backend.service.impl;

import com.wms.backend.domain.MovimientoAlmacen;
import com.wms.backend.domain.Producto;
import com.wms.backend.domain.StockUbicacion;
import com.wms.backend.domain.TipoMovimiento;
import com.wms.backend.domain.UbicacionAlmacen;
import com.wms.backend.dto.MovimientoRequestDTO;
import com.wms.backend.dto.MovimientoResponseDTO;
import com.wms.backend.exception.InsufficientStockException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.repository.MovimientoAlmacenRepository;
import com.wms.backend.repository.ProductoRepository;
import com.wms.backend.repository.StockUbicacionRepository;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.MovimientoAlmacenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MovimientoAlmacenServiceImpl implements MovimientoAlmacenService {

    private final MovimientoAlmacenRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final StockUbicacionRepository stockUbicacionRepository;

    @Override
    @Transactional
    public MovimientoResponseDTO registrarMovimiento(MovimientoRequestDTO requestDTO) {
        Producto producto = productoRepository.findById(requestDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + requestDTO.getProductoId()));

        UbicacionAlmacen ubicacion = ubicacionRepository.findById(requestDTO.getUbicacionId())
                .filter(u -> Boolean.TRUE.equals(u.getActiva()))
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada o inactiva con el ID: " + requestDTO.getUbicacionId()));

        int stockAnterior = producto.getStock();
        int stockResultante;

        // 1. Ejecutar actualización física por ubicación primero para obtener la diferencia real de stock global
        int diferenciaStockGlobal = actualizarStockUbicacion(producto, ubicacion, requestDTO);

        // 2. Calcular el stock resultante global exacto
        switch (requestDTO.getTipoMovimiento()) {
            case ENTRADA:
                stockResultante = stockAnterior + requestDTO.getCantidad();
                break;
            case SALIDA:
                if (stockAnterior < requestDTO.getCantidad()) {
                    throw new InsufficientStockException(
                            String.format("Stock global insuficiente. Stock actual: %d, Cantidad solicitada: %d",
                                    stockAnterior, requestDTO.getCantidad())
                    );
                }
                stockResultante = stockAnterior - requestDTO.getCantidad();
                break;
            case REUBICACION:
                stockResultante = stockAnterior;
                break;
            case AJUSTE_INVENTARIO:
                stockResultante = stockAnterior + diferenciaStockGlobal;
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento no válido: " + requestDTO.getTipoMovimiento());
        }

        // 3. Persistir cambio global en el producto
        if (!TipoMovimiento.REUBICACION.equals(requestDTO.getTipoMovimiento())) {
            producto.setStock(stockResultante);
            productoRepository.save(producto);
        }

        // 4. Registrar la trazabilidad del movimiento
        MovimientoAlmacen movimiento = MovimientoAlmacen.builder()
                .producto(producto)
                .ubicacion(ubicacion)
                .tipoMovimiento(requestDTO.getTipoMovimiento())
                .cantidad(requestDTO.getCantidad())
                .stockAnterior(stockAnterior)
                .stockResultante(stockResultante)
                .motivo(requestDTO.getMotivo())
                .usuario(requestDTO.getUsuario())
                .build();

        MovimientoAlmacen movimientoGuardado = movimientoRepository.save(movimiento);

        return mapToDTO(movimientoGuardado);
    }

    private int actualizarStockUbicacion(Producto producto, UbicacionAlmacen ubicacionDestino, MovimientoRequestDTO requestDTO) {
        TipoMovimiento tipo = requestDTO.getTipoMovimiento();
        Integer cantidad = requestDTO.getCantidad();
        int diferenciaGlobal = 0;

        if (TipoMovimiento.REUBICACION.equals(tipo)) {
            if (requestDTO.getUbicacionOrigenId() == null) {
                throw new IllegalArgumentException("Debe proporcionar la ubicación de origen para una reubicación.");
            }

            if (requestDTO.getUbicacionOrigenId().equals(ubicacionDestino.getId())) {
                throw new IllegalArgumentException("La ubicación de origen y destino no pueden ser iguales.");
            }

            UbicacionAlmacen ubicacionOrigen = ubicacionRepository.findById(requestDTO.getUbicacionOrigenId())
                    .filter(u -> Boolean.TRUE.equals(u.getActiva()))
                    .orElseThrow(() -> new ResourceNotFoundException("Ubicación de origen no encontrada o inactiva ID: " + requestDTO.getUbicacionOrigenId()));

            StockUbicacion stockOrigen = stockUbicacionRepository
                    .findByProductoIdAndUbicacionId(producto.getId(), ubicacionOrigen.getId())
                    .orElseThrow(() -> new InsufficientStockException("No existe registro de stock en la ubicación de origen."));

            if (stockOrigen.getCantidad() < cantidad) {
                throw new InsufficientStockException(
                        String.format("Stock insuficiente en origen (%s). Disponible: %d, Solicitado: %d",
                                ubicacionOrigen.getCodigoUbicacion(), stockOrigen.getCantidad(), cantidad)
                );
            }

            // Descontar origen
            stockOrigen.setCantidad(stockOrigen.getCantidad() - cantidad);
            if (stockOrigen.getCantidad() == 0) {
                stockUbicacionRepository.delete(stockOrigen);
                actualizarEstadoOcupadoUbicacion(ubicacionOrigen);
            } else {
                stockUbicacionRepository.save(stockOrigen);
            }

            // Aumentar destino
            StockUbicacion stockDestino = stockUbicacionRepository
                    .findByProductoIdAndUbicacionId(producto.getId(), ubicacionDestino.getId())
                    .orElseGet(() -> StockUbicacion.builder()
                            .producto(producto)
                            .ubicacion(ubicacionDestino)
                            .cantidad(0)
                            .build());

            stockDestino.setCantidad(stockDestino.getCantidad() + cantidad);
            stockUbicacionRepository.save(stockDestino);

            ubicacionDestino.setOcupada(true);
            ubicacionRepository.save(ubicacionDestino);

            return 0;
        }

        StockUbicacion stockUbicacion = stockUbicacionRepository
                .findByProductoIdAndUbicacionId(producto.getId(), ubicacionDestino.getId())
                .orElseGet(() -> StockUbicacion.builder()
                        .producto(producto)
                        .ubicacion(ubicacionDestino)
                        .cantidad(0)
                        .build());

        int cantidadActual = stockUbicacion.getCantidad();

        if (TipoMovimiento.ENTRADA.equals(tipo)) {
            stockUbicacion.setCantidad(cantidadActual + cantidad);
            stockUbicacionRepository.save(stockUbicacion);

            ubicacionDestino.setOcupada(true);
            ubicacionRepository.save(ubicacionDestino);

        } else if (TipoMovimiento.SALIDA.equals(tipo)) {
            if (cantidadActual < cantidad) {
                throw new InsufficientStockException(
                        String.format("Stock insuficiente en la ubicación %s. Disponible en rack: %d, Solicitado: %d",
                                ubicacionDestino.getCodigoUbicacion(), cantidadActual, cantidad)
                );
            }

            stockUbicacion.setCantidad(cantidadActual - cantidad);
            if (stockUbicacion.getCantidad() == 0) {
                stockUbicacionRepository.delete(stockUbicacion);
                actualizarEstadoOcupadoUbicacion(ubicacionDestino);
            } else {
                stockUbicacionRepository.save(stockUbicacion);
            }

        } else if (TipoMovimiento.AJUSTE_INVENTARIO.equals(tipo)) {
            diferenciaGlobal = cantidad - cantidadActual;

            if (cantidad == 0) {
                stockUbicacionRepository.delete(stockUbicacion);
                actualizarEstadoOcupadoUbicacion(ubicacionDestino);
            } else {
                stockUbicacion.setCantidad(cantidad);
                stockUbicacionRepository.save(stockUbicacion);
                ubicacionDestino.setOcupada(true);
                ubicacionRepository.save(ubicacionDestino);
            }
        }

        return diferenciaGlobal;
    }

    private void actualizarEstadoOcupadoUbicacion(UbicacionAlmacen ubicacion) {
        // Verificar si existen otros productos en la misma ubicación antes de liberar el flag ocupada
        List<StockUbicacion> productosEnUbicacion = stockUbicacionRepository.findByUbicacionId(ubicacion.getId());
        if (productosEnUbicacion.isEmpty()) {
            ubicacion.setOcupada(false);
            ubicacionRepository.save(ubicacion);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerTodosLosMovimientos() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerKardexPorProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no encontrado con el ID: " + productoId);
        }

        return movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerKardexPorUbicacion(Long ubicacionId) {
        if (!ubicacionRepository.existsById(ubicacionId)) {
            throw new ResourceNotFoundException("Ubicación no encontrada con el ID: " + ubicacionId);
        }

        return movimientoRepository.findByUbicacionIdOrderByFechaMovimientoDesc(ubicacionId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosFiltrados(
            Long productoId,
            TipoMovimiento tipo,
            Long ubicacionId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : null;

        Specification<MovimientoAlmacen> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productoId != null) {
                predicates.add(cb.equal(root.get("producto").get("id"), productoId));
            }
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipoMovimiento"), tipo));
            }
            if (ubicacionId != null) {
                predicates.add(cb.equal(root.get("ubicacion").get("id"), ubicacionId));
            }
            if (inicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaMovimiento"), inicio));
            }
            if (fin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaMovimiento"), fin));
            }

            query.orderBy(cb.desc(root.get("fechaMovimiento")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<MovimientoAlmacen> movimientos = movimientoRepository.findAll(spec);

        return movimientos.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private MovimientoResponseDTO mapToDTO(MovimientoAlmacen movimiento) {
        MovimientoResponseDTO.MovimientoResponseDTOBuilder builder = MovimientoResponseDTO.builder()
                .id(movimiento.getId())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .cantidad(movimiento.getCantidad())
                .stockAnterior(movimiento.getStockAnterior())
                .stockResultante(movimiento.getStockResultante())
                .motivo(movimiento.getMotivo())
                .usuario(movimiento.getUsuario())
                .fechaMovimiento(movimiento.getFechaMovimiento());

        if (movimiento.getProducto() != null) {
            builder.productoId(movimiento.getProducto().getId())
                    .productoCodigoSku(movimiento.getProducto().getCodigoSku())
                    .productoNombre(movimiento.getProducto().getNombre());
        }

        if (movimiento.getUbicacion() != null) {
            UbicacionAlmacen u = movimiento.getUbicacion();
            builder.ubicacionId(u.getId())
                    .ubicacionCodigo(u.getCodigoUbicacion())
                    .ubicacionPasillo(u.getPasillo())
                    .ubicacionRack(u.getRack());
        }

        return builder.build();
    }
}