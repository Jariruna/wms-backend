package com.wms.backend.dto;

import com.wms.backend.domain.Rol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRegistroDTO {
    private String username;
    private String password;
    private String nombreCompleto;
    private String email;
    private Rol rol;
}