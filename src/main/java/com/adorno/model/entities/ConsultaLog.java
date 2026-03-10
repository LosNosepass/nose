package com.adorno.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mapea la tabla `consultas_log` del SQL:
 *
 * CREATE TABLE `consultas_log` (
 *   `id`             int(11)    PK AUTO_INCREMENT,
 *   `id_movil`       int(11)    NOT NULL  FK → moviles(id) ON DELETE CASCADE,
 *   `id_usuario`     int(11)    DEFAULT NULL,
 *   `fecha_consulta` timestamp  DEFAULT current_timestamp()
 * )
 *
 * Se usa para registrar qué móviles se consultan y calcular tendencias.
 */
@Entity
@Table(name = "consultas_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * FK a la tabla moviles (ON DELETE CASCADE).
     * id_usuario puede ser null si el visitante no está autenticado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movil", nullable = false,
                foreignKey = @ForeignKey(name = "fk_movil_consulta"))
    private Movil movil;

    /**
     * id_usuario: null si es un visitante invitado.
     */
    @Column(name = "id_usuario")
    private Integer idUsuario;

    /**
     * Timestamp automático de MariaDB (current_timestamp).
     * Se inserta automáticamente al crear el registro.
     */
    @Column(name = "fecha_consulta",
            columnDefinition = "timestamp default current_timestamp",
            insertable = false, updatable = false)
    private LocalDateTime fechaConsulta;
}
