package com.wms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.domain.Rol;
import com.wms.backend.dto.auth.JwtResponseDTO;
import com.wms.backend.dto.auth.LoginRequestDTO;
import com.wms.backend.dto.auth.RegistroRequestDTO;
import com.wms.backend.security.CustomUserDetailsService;
import com.wms.backend.security.JwtUtils;
import com.wms.backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("POST /api/v1/auth/login - Debe retornar 200 OK y el token JWT si las credenciales son válidas")
    void login_Exito() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("jninaco")
                .password("password123")
                .build();

        JwtResponseDTO responseDTO = JwtResponseDTO.builder()
                .token("mocked.jwt.token")
                .id(1L)
                .username("jninaco")
                .email("jninaco@wms.com")
                .roles(List.of("ROLE_ADMIN"))
                .build();

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.username").value("jninaco"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Debe retornar 201 CREATED al registrar un usuario")
    void register_Exito() throws Exception {
        RegistroRequestDTO request = RegistroRequestDTO.builder()
                .username("jninaco")
                .password("password123")
                .nombreCompleto("José Luis Ninaco")
                .email("jninaco@wms.com")
                .rol(Rol.ROLE_ADMIN)
                .build();

        when(authService.registrarUsuario(any(RegistroRequestDTO.class)))
                .thenReturn("Usuario registrado exitosamente");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuario registrado exitosamente"));
    }
}