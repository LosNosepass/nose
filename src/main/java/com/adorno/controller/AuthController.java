package com.adorno.controller;

import com.adorno.configuration.security.jwt.JWTUtils;
import com.adorno.model.dtos.JwtResponseDTO;
import com.adorno.model.dtos.LoginRequestDTO;
import com.adorno.model.dtos.UserCreateDTO;
import com.adorno.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final UserService userService;

    /**
     * POST /api/auth/login
     * Autentica usuario y devuelve token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new JwtResponseDTO(token, userDetails.getUsername(), roles));
    }

    /**
     * POST /api/auth/register
     * Registra un nuevo usuario (por defecto con ROLE_USER).
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserCreateDTO dto) {
        userService.register(dto);
        return ResponseEntity.ok("Usuario registrado correctamente: " + dto.username());
    }
}
