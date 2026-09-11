package com.wms.backend.service.impl;

import com.wms.backend.domain.UbicacionAlmacen;
import com.wms.backend.dto.UbicacionRequestDTO;
import com.wms.backend.dto.UbicacionResponseDTO;
import com.wms.backend.exception.ResourceAlreadyExistsException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.mapper.UbicacionMapper;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final UbicacionMapper ubicacionMapper;

    @Override
    @Transactional
    public UbicacionResponseDTO crearUbicacion(UbicacionRequestDTO requestDTO) {
        if (ubicacionRepository.existsByCodigoUbicacion(requestDTO.getCodigoUbicacion())) {
            throw new ResourceAlreadyExistsException(
                    "Ya existe una ubicación registrada con el código: " + requestDTO.getCodigoUbicacion()
            );
        }

        UbicacionAlmacen ubicacion = ubicacionMapper.toEntity(requestDTO);
        UbicacionAlmacen guardada = ubicacionRepository.save(ubicacion);
        return ubicacionMapper.toDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public UbicacionResponseDTO obtenerPorId(Long id) {
        UbicacionAlmacen ubicacion = buscarPorIdOLanzar(id);
        return ubicacionMapper.toDTO(ubicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public UbicacionResponseDTO obtenerPorCodigo(String codigoUbicacion) {
        UbicacionAlmacen ubicacion = ubicacionRepository.findByCodigoUbicacion(codigoUbicacion)
                .filter(UbicacionAlmacen::getActiva)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con el código: " + codigoUbicacion));
        return ubicacionMapper.toDTO(ubicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionResponseDTO> obtenerTodas() {
        return ubicacionRepository.findByActivaTrue().stream()
                .map(ubicacionMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionResponseDTO> obtenerPorEstadoOcupacion(Boolean ocupada) {
        return ubicacionRepository.findByOcupadaAndActivaTrue(ocupada).stream()
                .map(ubicacionMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionResponseDTO> obtenerPorPasilloYRack(String pasillo, String rack) {
        return ubicacionRepository.findByPasilloAndRackAndActivaTrue(pasillo, rack).stream()
                .map(ubicacionMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public UbicacionResponseDTO actualizarUbicacion(Long id, UbicacionRequestDTO requestDTO) {
        UbicacionAlmacen ubicacion = buscarPorIdOLanzar(id);

        if (!ubicacion.getCodigoUbicacion().equals(requestDTO.getCodigoUbicacion())
                && ubicacionRepository.existsByCodigoUbicacion(requestDTO.getCodigoUbicacion())) {
            throw new ResourceAlreadyExistsException(
                    "Ya existe otra ubicación con el código: " + requestDTO.getCodigoUbicacion()
            );
        }

        ubicacionMapper.updateEntityFromDTO(requestDTO, ubicacion);
        UbicacionAlmacen actualizada = ubicacionRepository.save(ubicacion);
        return ubicacionMapper.toDTO(actualizada);
    }

    @Override
    @Transactional
    public UbicacionResponseDTO cambiarEstadoOcupacion(Long id, Boolean ocupada) {
        UbicacionAlmacen ubicacion = buscarPorIdOLanzar(id);
        ubicacion.setOcupada(ocupada);
        UbicacionAlmacen actualizada = ubicacionRepository.save(ubicacion);
        return ubicacionMapper.toDTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminarUbicacion(Long id) {
        UbicacionAlmacen ubicacion = buscarPorIdOLanzar(id);
        ubicacion.setActiva(false); // Eliminación lógica
        ubicacionRepository.save(ubicacion);
    }

    private UbicacionAlmacen buscarPorIdOLanzar(Long id) {
        return ubicacionRepository.findById(id)
                .filter(UbicacionAlmacen::getActiva)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + id));
    }
}