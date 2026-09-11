package com.wms.backend.service;

import com.wms.backend.domain.UbicacionAlmacen;
import com.wms.backend.dto.UbicacionRequestDTO;
import com.wms.backend.dto.UbicacionResponseDTO;
import com.wms.backend.exception.ResourceAlreadyExistsException;
import com.wms.backend.exception.ResourceNotFoundException;
import com.wms.backend.mapper.UbicacionMapper;
import com.wms.backend.repository.UbicacionRepository;
import com.wms.backend.service.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UbicacionServiceImplTest {

    @Mock
    private UbicacionRepository ubicacionRepository;

    @Spy
    private UbicacionMapper ubicacionMapper = new UbicacionMapper();

    @InjectMocks
    private UbicacionServiceImpl ubicacionService;

    private UbicacionRequestDTO requestDTO;
    private UbicacionAlmacen ubicacionEntidad;

    @BeforeEach
    void setUp() {
        requestDTO = UbicacionRequestDTO.builder()
                .codigoUbicacion("PAS-01-R02-N3-P01")
                .pasillo("01")
                .rack("02")
                .nivel("3")
                .posicion("01")
                .capacidadMaxima(100)
                .build();

        ubicacionEntidad = UbicacionAlmacen.builder()
                .id(1L)
                .codigoUbicacion("PAS-01-R02-N3-P01")
                .pasillo("01")
                .rack("02")
                .nivel("3")
                .posicion("01")
                .capacidadMaxima(100)
                .ocupada(false)
                .activa(true)
                .build();
    }

    @Test
    @DisplayName("Debe crear una ubicación exitosamente si el código no existe")
    void crearUbicacion_Exito() {
        when(ubicacionRepository.existsByCodigoUbicacion(requestDTO.getCodigoUbicacion())).thenReturn(false);
        when(ubicacionRepository.save(any(UbicacionAlmacen.class))).thenReturn(ubicacionEntidad);

        UbicacionResponseDTO resultado = ubicacionService.crearUbicacion(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigoUbicacion()).isEqualTo("PAS-01-R02-N3-P01");
        verify(ubicacionRepository, times(1)).save(any(UbicacionAlmacen.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceAlreadyExistsException cuando el código de ubicación ya está registrado")
    void crearUbicacion_CodigoDuplicado_LanzaExcepcion() {
        when(ubicacionRepository.existsByCodigoUbicacion(requestDTO.getCodigoUbicacion())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> ubicacionService.crearUbicacion(requestDTO));
        verify(ubicacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar la ubicación por ID si existe y está activa")
    void obtenerPorId_Exito() {
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacionEntidad));

        UbicacionResponseDTO resultado = ubicacionService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando la ubicación no existe o está inactiva")
    void obtenerPorId_NoEncontrado_LanzaExcepcion() {
        when(ubicacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ubicacionService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Debe realizar eliminación lógica actualizando el campo activo a false")
    void eliminarUbicacion_Exito() {
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacionEntidad));

        ubicacionService.eliminarUbicacion(1L);

        assertThat(ubicacionEntidad.getActiva()).isFalse();
        verify(ubicacionRepository, times(1)).save(ubicacionEntidad);
    }
}