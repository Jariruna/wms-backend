package com.wms.backend.service.impl;

import com.wms.backend.domain.Usuario;
import com.wms.backend.dto.auth.JwtResponseDTO;
import com.wms.backend.dto.auth.LoginRequestDTO;
import com.wms.backend.dto.auth.RegistroRequestDTO;
import com.wms.backend.repository.UsuarioRepository;
import com.wms.backend.security.JwtUtils;
import com.wms.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generarToken(authentication);

        Usuario usuarioDetails = (Usuario) authentication.getPrincipal();
        List<String> roles = usuarioDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JwtResponseDTO.builder()
                .token(jwt)
                .id(usuarioDetails.getId())
                .username(usuarioDetails.getUsername())
                .email(usuarioDetails.getEmail())
                .nombreCompleto(usuarioDetails.getNombreCompleto())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public String registrarUsuario(RegistroRequestDTO registroRequest) {
        if (usuarioRepository.existsByUsername(registroRequest.getUsername())) {
            throw new IllegalArgumentException("Error: El nombre de usuario ya está en uso");
        }

        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            throw new IllegalArgumentException("Error: El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .username(registroRequest.getUsername())
                .password(passwordEncoder.encode(registroRequest.getPassword()))
                .nombreCompleto(registroRequest.getNombreCompleto())
                .email(registroRequest.getEmail())
                .rol(registroRequest.getRol())
                .activo(true)
                .build();

        usuarioRepository.save(usuario);
        return "Usuario registrado exitosamente";
    }
}