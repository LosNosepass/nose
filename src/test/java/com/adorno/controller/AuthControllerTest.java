package com.adorno.controller;

import com.adorno.model.dtos.UserCreateDTO;
import com.adorno.model.dtos.LoginRequestDTO;
import com.adorno.services.UserService;
import com.adorno.configuration.security.jwt.JWTUtils;
import com.adorno.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración del AuthController.
 * Prueba los endpoints de login y registro.
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController — Tests de integración")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    // ─── LOGIN ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Login correcto debe devolver 200 con token JWT")
        void loginCorrecto() throws Exception {
            var userDetails = new User("admin", "encoded",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            var auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(jwtUtils.generateToken(any())).thenReturn("mock.jwt.token");

            LoginRequestDTO request = new LoginRequestDTO("admin", "admin123");

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                    .andExpect(jsonPath("$.type").value("Bearer"))
                    .andExpect(jsonPath("$.username").value("admin"))
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
        }

        @Test
        @DisplayName("Login con credenciales incorrectas debe devolver 403")
        void loginIncorrecto() throws Exception {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Credenciales inválidas"));

            LoginRequestDTO request = new LoginRequestDTO("admin", "wrongpassword");

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Login sin username debe devolver 400")
        void loginSinUsername() throws Exception {
            LoginRequestDTO request = new LoginRequestDTO("", "admin123");

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── REGISTRO ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/auth/register")
    class RegisterTests {

        @Test
        @DisplayName("Registro correcto debe devolver 200 con mensaje de confirmación")
        void registroCorrecto() throws Exception {
            UserCreateDTO dto = new UserCreateDTO(
                    "nuevoUsuario", "nuevo@test.com", "password123", Set.of("usuario")
            );

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("nuevoUsuario")));
        }

        @Test
        @DisplayName("Registro sin email válido debe devolver 400")
        void registroEmailInvalido() throws Exception {
            UserCreateDTO dto = new UserCreateDTO(
                    "usuario1", "esto-no-es-un-email", "password123", null
            );

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Registro con contraseña corta debe devolver 400")
        void registroPasswordCorta() throws Exception {
            UserCreateDTO dto = new UserCreateDTO(
                    "usuario1", "u@test.com", "123", null // menos de 6 caracteres
            );

            mockMvc.perform(post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
