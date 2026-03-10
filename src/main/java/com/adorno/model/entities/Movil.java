package com.adorno.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que mapea exactamente la tabla `moviles` del SQL proporcionado.
 *
 * Columnas SQL → campos Java:
 *   cpu_tipo            → cpuTipo
 *   cpu_nucleos         → cpuNucleos
 *   cpu_ghz             → cpuGhz
 *   pantalla_pulgadas   → pantallaPulgadas
 *   pantalla_tecnologia → pantallaTecnologia
 *   dim_alto/ancho/grosor → dimAlto/Ancho/Grosor
 *   camara_capacidad    → camaraCapacidad (String)
 *   consultas           → consultas (contador tendencias)
 */
@Entity
@Table(name = "moviles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String marca;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String modelo;

    // ── Procesador ────────────────────────────────────────────────────────────
    @Column(name = "cpu_tipo", length = 50)
    private String cpuTipo;

    @Column(name = "cpu_nucleos")
    private Integer cpuNucleos;

    @Column(name = "cpu_ghz", precision = 4, scale = 2)
    private BigDecimal cpuGhz;

    // ── Almacenamiento ────────────────────────────────────────────────────────
    @Column(name = "almacenamiento_gb")
    private Integer almacenamientoGb;

    // ── RAM ───────────────────────────────────────────────────────────────────
    @NotNull
    @Column(name = "ram_gb", nullable = false)
    private Integer ramGb;

    // ── Pantalla ──────────────────────────────────────────────────────────────
    @Column(name = "pantalla_pulgadas", precision = 4, scale = 2)
    private BigDecimal pantallaPulgadas;

    @Column(name = "pantalla_tecnologia", length = 50)
    private String pantallaTecnologia;

    // ── Dimensiones (cm) ──────────────────────────────────────────────────────
    @Column(name = "dim_alto", precision = 5, scale = 2)
    private BigDecimal dimAlto;

    @Column(name = "dim_ancho", precision = 5, scale = 2)
    private BigDecimal dimAncho;

    @Column(name = "dim_grosor", precision = 5, scale = 2)
    private BigDecimal dimGrosor;

    // ── Peso ──────────────────────────────────────────────────────────────────
    @Column(name = "peso_gr")
    private Integer pesoGr;

    // ── Cámara (String porque puede ser "50+12+10 Mpx") ──────────────────────
    @Column(name = "camara_capacidad", length = 100)
    private String camaraCapacidad;

    // ── Batería ───────────────────────────────────────────────────────────────
    @Column(name = "bateria_mah")
    private Integer bateriaMah;

    // ── NFC (tinyint(1) en MariaDB → Boolean en Java) ─────────────────────────
    @Column(name = "nfc", columnDefinition = "tinyint(1) default 0")
    @Builder.Default
    private Boolean nfc = false;

    // ── Precio ────────────────────────────────────────────────────────────────
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // ── Fecha lanzamiento ─────────────────────────────────────────────────────
    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    // ── Contador de consultas (para tendencias) ───────────────────────────────
    @Column(name = "consultas", columnDefinition = "int default 0")
    @Builder.Default
    private Integer consultas = 0;
}
