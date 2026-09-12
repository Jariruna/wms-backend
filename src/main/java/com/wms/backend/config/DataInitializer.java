package com.wms.backend.config;

import com.wms.backend.domain.Rol;
import com.wms.backend.domain.Usuario;
import com.wms.backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Usuario user = userRepository.findByUsername("jninaco")
                .orElse(new Usuario());

        user.setUsername("jninaco");
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setEmail("jninaco@wms.com");
        user.setNombreCompleto("José Luis Ninaco");
        user.setActivo(true);
        user.setFechaCreacion(LocalDateTime.now());

        // Asignación usando el enum exacto de tu proyecto
        user.setRol(Rol.ROLE_ADMIN);

        userRepository.save(user);

        System.out.println("\n=================================================");
        System.out.println(">>> USUARIO RESETEADO/CREADO EXITOSAMENTE");
        System.out.println(">>> Username: jninaco");
        System.out.println(">>> Email: jninaco@wms.com");
        System.out.println(">>> Rol: ROLE_ADMIN");
        System.out.println(">>> Password: admin123");
        System.out.println("=================================================\n");
    }
}