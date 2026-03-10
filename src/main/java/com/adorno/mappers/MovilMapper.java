package com.adorno.mappers;

import com.adorno.model.dtos.MovilCreateDTO; 
import com.adorno.model.dtos.MovilDetalleDTO;
import com.adorno.model.dtos.MovilResumenDTO;
import com.adorno.model.entities.Movil;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * MapStruct no puede generar implementaciones para Java Records directamente
 * cuando hay ambigüedad con ArrayList o tipos complejos.
 * Se usa implementación manual para los DTOs que son records.
 */
@Mapper(componentModel = "spring")
public abstract class MovilMapper {

    // ── DTO → Entidad (para CREATE) ───────────────────────────────────────────
    public Movil toEntity(MovilCreateDTO dto) {
        if (dto == null) return null;
        return Movil.builder()
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .cpuTipo(dto.getCpuTipo())
                .cpuNucleos(dto.getCpuNucleos())
                .cpuGhz(dto.getCpuGhz())
                .almacenamientoGb(dto.getAlmacenamientoGb())
                .ramGb(dto.getRamGb())
                .pantallaPulgadas(dto.getPantallaPulgadas())
                .pantallaTecnologia(dto.getPantallaTecnologia())
                .dimAlto(dto.getDimAlto())
                .dimAncho(dto.getDimAncho())
                .dimGrosor(dto.getDimGrosor())
                .pesoGr(dto.getPesoGr())
                .camaraCapacidad(dto.getCamaraCapacidad())
                .bateriaMah(dto.getBateriaMah())
                .nfc(dto.getNfc() != null ? dto.getNfc() : false)
                .precio(dto.getPrecio())
                .fechaLanzamiento(dto.getFechaLanzamiento())
                .consultas(0)
                .build();
    }

    // ── Entidad → DTO detalle completo ────────────────────────────────────────
    public MovilDetalleDTO toDetalleDTO(Movil m) {
        if (m == null) return null;
        return new MovilDetalleDTO(
                m.getId(),
                m.getMarca(),
                m.getModelo(),
                m.getCpuTipo(),
                m.getCpuNucleos(),
                m.getCpuGhz(),
                m.getAlmacenamientoGb(),
                m.getRamGb(),
                m.getPantallaPulgadas(),
                m.getPantallaTecnologia(),
                m.getDimAlto(),
                m.getDimAncho(),
                m.getDimGrosor(),
                m.getPesoGr(),
                m.getCamaraCapacidad(),
                m.getBateriaMah(),
                m.getNfc(),
                m.getPrecio(),
                m.getFechaLanzamiento()
        );
    }

    // ── Entidad → DTO resumen (para listas y tendencias) ──────────────────────
    public MovilResumenDTO toResumenDTO(Movil m) {
        if (m == null) return null;
        return new MovilResumenDTO(
                m.getId(),
                m.getMarca(),
                m.getModelo(),
                m.getCpuNucleos(),
                m.getRamGb(),
                m.getAlmacenamientoGb(),
                m.getPrecio()
        );
    }

    // ── DTO → Entidad existente (para UPDATE / PUT) ────────────────────────────
    public void updateEntityFromDTO(MovilCreateDTO dto, @MappingTarget Movil movil) {
        if (dto == null) return;
        movil.setMarca(dto.getMarca());
        movil.setModelo(dto.getModelo());
        movil.setCpuTipo(dto.getCpuTipo());
        movil.setCpuNucleos(dto.getCpuNucleos());
        movil.setCpuGhz(dto.getCpuGhz());
        movil.setAlmacenamientoGb(dto.getAlmacenamientoGb());
        movil.setRamGb(dto.getRamGb());
        movil.setPantallaPulgadas(dto.getPantallaPulgadas());
        movil.setPantallaTecnologia(dto.getPantallaTecnologia());
        movil.setDimAlto(dto.getDimAlto());
        movil.setDimAncho(dto.getDimAncho());
        movil.setDimGrosor(dto.getDimGrosor());
        movil.setPesoGr(dto.getPesoGr());
        movil.setCamaraCapacidad(dto.getCamaraCapacidad());
        movil.setBateriaMah(dto.getBateriaMah());
        movil.setNfc(dto.getNfc() != null ? dto.getNfc() : false);
        movil.setPrecio(dto.getPrecio());
        movil.setFechaLanzamiento(dto.getFechaLanzamiento());
    }
}

