package com.wms.backend.service;

import com.wms.backend.dto.auth.JwtResponseDTO;
import com.wms.backend.dto.auth.LoginRequestDTO;
import com.wms.backend.dto.auth.RegistroRequestDTO;

public interface AuthService {
    JwtResponseDTO login(LoginRequestDTO loginRequest);
    String registrarUsuario(RegistroRequestDTO registroRequest);
}