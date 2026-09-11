package com.wms.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String secretClave = "ClaveSecretaDePruebaParaUnitTestsQueSuperaLos32BytesRequeridos!";
    private final long expiracionMs = 3600000; // 1 hora

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(secretClave, expiracionMs);
    }

    @Test
    @DisplayName("Debe generar un token JWT válido y extraer el username correctamente")
    void generarYValidarToken_Exito() {
        String username = "jninaco";

        String token = jwtUtils.generarTokenDesdeUsername(username);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtUtils.validarToken(token)).isTrue();
        assertThat(jwtUtils.getUsernameDesdeToken(token)).isEqualTo(username);
    }

    @Test
    @DisplayName("Debe retornar false al validar un token malformado o alterado")
    void validarToken_TokenInvalido_RetornaFalse() {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiJ9.token.alterado";

        boolean esValido = jwtUtils.validarToken(tokenInvalido);

        assertThat(esValido).isFalse();
    }

    @Test
    @DisplayName("Debe retornar false para un token ya expirado")
    void validarToken_TokenExpirado_RetornaFalse() {
        // JwtUtils configurado con expiración inmediata (0 ms)
        JwtUtils jwtUtilsExpirado = new JwtUtils(secretClave, -1000);
        String tokenExpirado = jwtUtilsExpirado.generarTokenDesdeUsername("jninaco");

        boolean esValido = jwtUtils.validarToken(tokenExpirado);

        assertThat(esValido).isFalse();
    }
}