package com.adorno.services;

import com.adorno.mappers.MovilMapper;
import com.adorno.model.dtos.MovilCreateDTO;
import com.adorno.model.dtos.MovilDetalleDTO;
import com.adorno.model.dtos.MovilResumenDTO;
import com.adorno.model.entities.ConsultaLog;
import com.adorno.model.entities.Movil;
import com.adorno.repositories.ConsultaLogRepository;
import com.adorno.repositories.MovilRepository;
import com.adorno.repositories.MovilSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovilService {

    private final MovilRepository movilRepository;
    private final ConsultaLogRepository consultaLogRepository;
    private final MovilMapper movilMapper;

    // ─── TENDENCIAS ──────────────────────────────────────────────────────────

    /**
     * Los 5 móviles más consultados para la página principal.
     */
    public List<MovilResumenDTO> getTendencias() {
        return movilRepository
                .findTop5ByConsultas(PageRequest.of(0, 5))
                .stream()
                .map(movilMapper::toResumenDTO)
                .toList();
    }

    // ─── DETALLE ─────────────────────────────────────────────────────────────

    /**
     * Detalle completo de un móvil.
     * Incrementa el contador `consultas` y registra en `consultas_log`.
     */
    @Transactional
    public MovilDetalleDTO getDetalle(Integer id) {
        Movil movil = findOrThrow(id);

        // Incrementar contador en tabla moviles
        movilRepository.incrementarConsultas(id);

        // Registrar en consultas_log (id_usuario null si es invitado)
        Integer idUsuario = getIdUsuarioActual();
        ConsultaLog log = ConsultaLog.builder()
                .movil(movil)
                .idUsuario(idUsuario)
                .build();
        consultaLogRepository.save(log);

        return movilMapper.toDetalleDTO(movil);
    }

    // ─── BÚSQUEDA ────────────────────────────────────────────────────────────

    /**
     * Búsqueda con criterios combinados. El precio (min/max) es obligatorio.
     */
    public List<MovilResumenDTO> buscar(
            String marca,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Integer ramMinGb,
            Integer ramMaxGb,
            Boolean nfc,
            String pantallaTecnologia
    ) {
        Specification<Movil> spec = MovilSpecification.buscar(
                marca, precioMin, precioMax, ramMinGb, ramMaxGb, nfc, pantallaTecnologia
        );
        return movilRepository.findAll(spec)
                .stream()
                .map(movilMapper::toResumenDTO)
                .toList();
    }

    public List<String> getMarcas() {
        return movilRepository.findDistinctMarcas();
    }

    public List<String> getTecnologiasPantalla() {
        return movilRepository.findDistinctTecnologias();
    }

    // ─── COMPARACIÓN ─────────────────────────────────────────────────────────

    /**
     * Devuelve los datos completos de dos móviles para compararlos side-by-side.
     */
    @Transactional
    public List<MovilDetalleDTO> comparar(Integer id1, Integer id2) {
        return List.of(getDetalle(id1), getDetalle(id2));
    }

    // ─── CRUD (ADMIN) ─────────────────────────────────────────────────────────

    public List<MovilResumenDTO> getAll() {
        return movilRepository.findAll()
                .stream()
                .map(movilMapper::toResumenDTO)
                .toList();
    }

    @Transactional
    public MovilDetalleDTO create(MovilCreateDTO dto) {
        Movil movil = movilMapper.toEntity(dto);
        movil.setConsultas(0);
        return movilMapper.toDetalleDTO(movilRepository.save(movil));
    }

    @Transactional
    public MovilDetalleDTO update(Integer id, MovilCreateDTO dto) {
        Movil movil = findOrThrow(id);
        movilMapper.updateEntityFromDTO(dto, movil);
        return movilMapper.toDetalleDTO(movilRepository.save(movil));
    }

    @Transactional
    public void delete(Integer id) {
        if (!movilRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Móvil no encontrado con id: " + id);
        }
        movilRepository.deleteById(id);
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private Movil findOrThrow(Integer id) {
        return movilRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Móvil no encontrado con id: " + id));
    }

    /**
     * Obtiene el id del usuario autenticado actual.
     * Devuelve null si es un invitado no autenticado.
     */
    private Integer getIdUsuarioActual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                // En un caso real se consultaría el id en BD por nombre
                return null; // simplificado: registrar sin id de usuario
            }
        } catch (Exception ignored) {}
        return null;
    }
}

