package com.adorno.model.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear / actualizar un móvil (solo ADMIN).
 * Los campos coinciden con la tabla `moviles` del SQL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovilCreateDTO {

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    // Procesador (cpu_tipo, cpu_nucleos, cpu_ghz)
    private String cpuTipo;
    private Integer cpuNucleos;
    private BigDecimal cpuGhz;

    // Almacenamiento
    private Integer almacenamientoGb;

    // RAM (obligatoria en la tabla)
    @NotNull(message = "La RAM es obligatoria")
    @Min(value = 1, message = "RAM mínima 1 GB")
    private Integer ramGb;

    // Pantalla
    private BigDecimal pantallaPulgadas;
    private String pantallaTecnologia;

    // Dimensiones (cm)
    private BigDecimal dimAlto;
    private BigDecimal dimAncho;
    private BigDecimal dimGrosor;

    // Peso
    private Integer pesoGr;

    // Cámara como texto ("50 Mpx + 12 Mpx")
    private String camaraCapacidad;

    // Batería
    private Integer bateriaMah;

    // NFC
    private Boolean nfc;

    // Precio (obligatorio en la tabla)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "Precio mínimo 0.01")
    private BigDecimal precio;

    private LocalDate fechaLanzamiento;
}

