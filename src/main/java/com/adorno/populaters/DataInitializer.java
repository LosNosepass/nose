package com.adorno.populaters;

import com.adorno.model.entities.Usuario;
import com.adorno.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializa datos básicos al arrancar la aplicación.
 * Crea el usuario admin por defecto si no existe en la tabla `usuarios`.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdminUser();
    }

    private void initAdminUser() {
        if (!usuarioRepository.existsByNombreUsuario("admin")) {
            Usuario admin = Usuario.builder()
                    .nombreUsuario("admin")
                    .email("admin@moviles.com")
                    .password(passwordEncoder.encode("admin123"))
                    .rol("admin")
                    .build();

            usuarioRepository.save(admin);
            log.info("✅ Usuario admin creado → usuario: admin | contraseña: admin123");
        } else {
            log.info("ℹ️  Usuario admin ya existe en la BD");
        }
    }
}

