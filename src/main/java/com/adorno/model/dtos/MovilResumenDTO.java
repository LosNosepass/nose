package com.adorno.model.dtos;

import java.math.BigDecimal;

/**
 * Info resumida para listados y página de tendencias.
 * Campos: modelo, marca, núcleos, ram, almacenamiento, precio.
 */
public record MovilResumenDTO(
        Integer id,
        String marca,
        String modelo,
        Integer cpuNucleos,
        Integer ramGb,
        Integer almacenamientoGb,
        BigDecimal precio
) {}
