package com.wms.backend.dto.auth;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDTO {

    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto; // <-- Añadido para traerlo desde la BD
    private List<String> roles;
}