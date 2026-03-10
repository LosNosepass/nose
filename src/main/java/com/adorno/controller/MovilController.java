package com.adorno.controller;

import com.adorno.model.dtos.MovilCreateDTO;
import com.adorno.model.dtos.MovilDetalleDTO;
import com.adorno.model.dtos.MovilResumenDTO;
import com.adorno.services.MovilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/moviles")
@RequiredArgsConstructor
public class MovilController {

    private final MovilService movilService;

    // ─────────────────────────────────────────────────────────────────────────
    // ENDPOINTS PÚBLICOS (GUEST / cualquier usuario)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/moviles/tendencias
     * Devuelve los 5 móviles más consultados con info resumida.
     * Es la página principal de presentación.
     */
    @GetMapping("/tendencias")
    public ResponseEntity<List<MovilResumenDTO>> getTendencias() {
        return ResponseEntity.ok(movilService.getTendencias());
    }

    /**
     * GET /api/moviles/marcas
     * Devuelve la lista de marcas disponibles.
     */
    @GetMapping("/marcas")
    public ResponseEntity<List<String>> getMarcas() {
        return ResponseEntity.ok(movilService.getMarcas());
    }

    /**
     * GET /api/moviles/tecnologias-pantalla
     * Devuelve las tecnologías de pantalla disponibles.
     */
    @GetMapping("/tecnologias-pantalla")
    public ResponseEntity<List<String>> getTecnologiasPantalla() {
        return ResponseEntity.ok(movilService.getTecnologiasPantalla());
    }

    /**
     * GET /api/moviles/buscar
     * Búsqueda con múltiples criterios. El precio es obligatorio.
     *
     * Parámetros opcionales:
     *   - marca              : filtrar por marca (obligatorio si se quiere buscar por marca)
     *   - precioMin          : precio mínimo (OBLIGATORIO)
     *   - precioMax          : precio máximo (OBLIGATORIO)
     *   - ramMin             : RAM mínima en GB
     *   - ramMax             : RAM máxima en GB
     *   - nfc                : true / false
     *   - pantallaTecnologia : AMOLED, OLED, IPS, LCD...
     *
     * Ejemplos:
     *   GET /api/moviles/buscar?marca=Samsung&precioMin=200&precioMax=800
     *   GET /api/moviles/buscar?precioMin=100&precioMax=500&ramMin=8&nfc=true
     *   GET /api/moviles/buscar?precioMin=0&precioMax=9999&pantallaTecnologia=AMOLED
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false) String marca,
            @RequestParam BigDecimal precioMin,
            @RequestParam BigDecimal precioMax,
            @RequestParam(required = false) Integer ramMin,
            @RequestParam(required = false) Integer ramMax,
            @RequestParam(required = false) Boolean nfc,
            @RequestParam(required = false) String pantallaTecnologia
    ) {
        List<MovilResumenDTO> resultado = movilService.buscar(
                marca, precioMin, precioMax, ramMin, ramMax, nfc, pantallaTecnologia
        );
        return ResponseEntity.ok(resultado);
    }

    /**
     * GET /api/moviles/{id}
     * Devuelve todas las características de un móvil concreto.
     * También incrementa el contador de consultas (para tendencias).
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovilDetalleDTO> getDetalle(@PathVariable Integer id) {
        return ResponseEntity.ok(movilService.getDetalle(id));
    }

    /**
     * GET /api/moviles/comparar?id1=X&id2=Y
     * Devuelve los datos completos de dos móviles para comparación side-by-side.
     */
    @GetMapping("/comparar")
    public ResponseEntity<List<MovilDetalleDTO>> comparar(
            @RequestParam Integer id1,
            @RequestParam Integer id2
    ) {
        return ResponseEntity.ok(movilService.comparar(id1, id2));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ENDPOINTS DE ADMINISTRADOR (CRUD completo)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/moviles
     * Lista todos los móviles (solo ADMIN).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovilResumenDTO>> getAll() {
        return ResponseEntity.ok(movilService.getAll());
    }

    /**
     * POST /api/moviles
     * Crea un nuevo móvil (solo ADMIN).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovilDetalleDTO> create(@Valid @RequestBody MovilCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movilService.create(dto));
    }

    /**
     * PUT /api/moviles/{id}
     * Actualiza un móvil completo (solo ADMIN).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovilDetalleDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody MovilCreateDTO dto
    ) {
        return ResponseEntity.ok(movilService.update(id, dto));
    }

    /**
     * DELETE /api/moviles/{id}
     * Elimina un móvil (solo ADMIN).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        movilService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
