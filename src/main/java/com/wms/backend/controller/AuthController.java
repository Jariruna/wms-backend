package com.wms.backend.controller;

import com.wms.backend.dto.auth.JwtResponseDTO;
import com.wms.backend.dto.auth.LoginRequestDTO;
import com.wms.backend.dto.auth.RegistroRequestDTO;
import com.wms.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para el inicio de sesión.
     * Retorna el JwtResponseDTO que ahora incluye el nombreCompleto desde la BD.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        JwtResponseDTO tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Endpoint para el registro de nuevos usuarios en el sistema WMS.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistroRequestDTO registroRequest) {
        String mensaje = authService.registrarUsuario(registroRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }
}