package com.adorno.services;

import com.adorno.model.dtos.UserCreateDTO;
import com.adorno.model.entities.Usuario;
import com.adorno.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en la tabla `usuarios`.
     * El rol por defecto es "invitado" si no se especifica.
     * Valores válidos de rol: "admin", "usuario", "invitado"
     */
    public Usuario register(UserCreateDTO dto) {
        if (usuarioRepository.existsByNombreUsuario(dto.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está en uso");
        }

        // Determina el rol: validar que sea uno de los del ENUM
        String rol = "invitado";
        if (dto.roles() != null && !dto.roles().isEmpty()) {
            String rolSolicitado = dto.roles().iterator().next().toLowerCase();
            if (rolSolicitado.equals("admin") || rolSolicitado.equals("usuario") || rolSolicitado.equals("invitado")) {
                rol = rolSolicitado;
            }
        }

        Usuario usuario = Usuario.builder()
                .nombreUsuario(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .rol(rol)
                .build();

        return usuarioRepository.save(usuario);
    }
}

