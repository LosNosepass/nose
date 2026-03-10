package com.adorno.model.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Información completa de un móvil para la vista de detalle.
 * Todos los campos de la tabla `moviles`.
 */
public record MovilDetalleDTO(
        Integer id,
        String marca,
        String modelo,

        // Procesador
        String cpuTipo,
        Integer cpuNucleos,
        BigDecimal cpuGhz,

        // Almacenamiento
        Integer almacenamientoGb,

        // RAM
        Integer ramGb,

        // Pantalla
        BigDecimal pantallaPulgadas,
        String pantallaTecnologia,

        // Dimensiones (cm)
        BigDecimal dimAlto,
        BigDecimal dimAncho,
        BigDecimal dimGrosor,

        // Peso
        Integer pesoGr,

        // Cámara
        String camaraCapacidad,

        // Batería
        Integer bateriaMah,

        // NFC
        Boolean nfc,

        // Precio y fecha
        BigDecimal precio,
        LocalDate fechaLanzamiento
) {}
