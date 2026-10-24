package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.EmpleadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import com.al_lado.al_lado_project.mappers.comunidad.PerfilEmpresarialMapper;
import com.al_lado.al_lado_project.mappers.trabajo.OportunidadLaboralResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmpleadorMapper {

    private final PerfilEmpresarialMapper perfilEmpresarialMapper;
    private final OportunidadLaboralResumenMapper oportunidadResumenMapper;

    public EmpleadorDTO toDTO(Empleador entity) {
        if (entity == null) return null;

        EmpleadorDTO dto = new EmpleadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setHabilitado(entity.isHabilitado());

        if (entity.getPerfilEmpresarial() != null) {
            dto.setPerfilEmpresarial(perfilEmpresarialMapper.toDTO(entity.getPerfilEmpresarial()));
        }

        if (entity.getOportunidades() != null) {
            dto.setOportunidades(entity.getOportunidades().stream()
                    .map(oportunidadResumenMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Proyección pública del directorio de empleadores (AL-SEC-006).
     *
     * <p>Mantiene nombre, estado, perfil empresarial público (sin CUIT ni el
     * supervisor) y el listado de ofertas, que ya son de lectura pública. Omite el
     * email y el teléfono.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO con la proyección pública, o {@code null}
     */
    public EmpleadorDTO toDTOPublic(Empleador entity) {
        if (entity == null) return null;

        EmpleadorDTO dto = new EmpleadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setHabilitado(entity.isHabilitado());

        if (entity.getPerfilEmpresarial() != null) {
            dto.setPerfilEmpresarial(perfilEmpresarialMapper.toDTOPublic(entity.getPerfilEmpresarial()));
        }

        if (entity.getOportunidades() != null) {
            dto.setOportunidades(entity.getOportunidades().stream()
                    .map(oportunidadResumenMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Empleador toEntity(EmpleadorDTO dto) {
        if (dto == null) return null;

        Empleador entity = new Empleador();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo. En edición el
        // servicio recarga la entidad existente y no pasa por aquí.
        entity.setId(null);
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}