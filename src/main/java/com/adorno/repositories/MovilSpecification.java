package com.adorno.repositories;

import com.adorno.model.entities.Movil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Especificaciones JPA para búsqueda dinámica sobre la tabla `moviles`.
 * Los nombres de campo coinciden con los de la entidad Movil (camelCase → SQL).
 */
public class MovilSpecification {

    private MovilSpecification() {}

    /**
     * Construye una Specification combinando todos los criterios opcionales.
     *
     * @param marca              Marca exacta (opcional, obligatorio si se busca por marca)
     * @param precioMin          Precio mínimo (OBLIGATORIO en todas las búsquedas)
     * @param precioMax          Precio máximo (OBLIGATORIO en todas las búsquedas)
     * @param ramMinGb           RAM mínima en GB (opcional)
     * @param ramMaxGb           RAM máxima en GB (opcional)
     * @param nfc                Tiene NFC: true/false (opcional)
     * @param pantallaTecnologia Tecnología de pantalla: AMOLED, IPS... (opcional)
     */
    public static Specification<Movil> buscar(
            String marca,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Integer ramMinGb,
            Integer ramMaxGb,
            Boolean nfc,
            String pantallaTecnologia
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Marca (case-insensitive)
            if (marca != null && !marca.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("marca")), marca.toLowerCase()));
            }

            // Precio mínimo (campo `precio` en la entidad)
            if (precioMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precio"), precioMin));
            }

            // Precio máximo
            if (precioMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precio"), precioMax));
            }

            // RAM mínima (campo `ramGb` → columna `ram_gb`)
            if (ramMinGb != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ramGb"), ramMinGb));
            }

            // RAM máxima
            if (ramMaxGb != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ramGb"), ramMaxGb));
            }

            // NFC (campo `nfc` → columna `nfc` tinyint)
            if (nfc != null) {
                predicates.add(cb.equal(root.get("nfc"), nfc));
            }

            // Tecnología de pantalla (campo `pantallaTecnologia` → columna `pantalla_tecnologia`)
            if (pantallaTecnologia != null && !pantallaTecnologia.isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(root.get("pantallaTecnologia")),
                        pantallaTecnologia.toLowerCase()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

