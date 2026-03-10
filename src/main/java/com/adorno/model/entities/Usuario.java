package com.adorno.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mapea la tabla `usuarios` del SQL:
 *
 * CREATE TABLE `usuarios` (
 *   `id`             int(11)       PK AUTO_INCREMENT,
 *   `nombre_usuario` varchar(50)   NOT NULL UNIQUE,
 *   `email`          varchar(100)  NOT NULL UNIQUE,
 *   `password`       varchar(255)  NOT NULL,
 *   `rol`            enum('admin','usuario','invitado') DEFAULT 'invitado'
 * )
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 50)
    private String nombreUsuario;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    /**
     * El ENUM de MariaDB ('admin','usuario','invitado') se mapea
     * como String para respetar exactamente los valores de la BD.
     * Valores posibles: "admin", "usuario", "invitado"
     */
    @Column(name = "rol", columnDefinition = "enum('admin','usuario','invitado') default 'invitado'")
    @Builder.Default
    private String rol = "invitado";
}
