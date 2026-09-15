package com.wms.backend.controller;

import com.wms.backend.domain.Usuario;
import com.wms.backend.dto.UsuarioRegistroDTO;
import com.wms.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody UsuarioRegistroDTO dto) {
        Usuario nuevoUsuario = Usuario.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombreCompleto(dto.getNombreCompleto())
                .email(dto.getEmail())
                .rol(dto.getRol())
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok(guardado);

    }
}