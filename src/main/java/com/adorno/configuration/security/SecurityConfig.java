package com.adorno.configuration.security;

import com.adorno.configuration.security.jwt.filters.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Endpoints PÚBLICOS ───────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/moviles/tendencias").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/moviles/marcas").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/moviles/tecnologias-pantalla").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/moviles/buscar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/moviles/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/moviles/comparar").permitAll()

                // ── CRUD solo ADMIN ───────────────────────────────────────────
                // Rol en BD: "admin" → Spring Security: "ROLE_ADMIN"
                .requestMatchers(HttpMethod.POST, "/api/moviles").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/moviles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/moviles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/moviles").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
