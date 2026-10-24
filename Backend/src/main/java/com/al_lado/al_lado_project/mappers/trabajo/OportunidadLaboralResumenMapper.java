package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.OportunidadLaboralResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import org.springframework.stereotype.Component;

@Component
public class OportunidadLaboralResumenMapper {

    public OportunidadLaboralResumenDTO toDTO(OportunidadLaboral entity) {
        if (entity == null) return null;
        OportunidadLaboralResumenDTO dto = new OportunidadLaboralResumenDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setUbicacion(entity.getUbicacion());
        dto.setModalidad(entity.getModalidad());
        dto.setRangoSalarial(entity.getRangoSalarial());
        dto.setActiva(entity.isActiva());
        dto.setFechaCreacion(entity.getFechaCreacion());
        return dto;
    }

    public OportunidadLaboral toEntity(OportunidadLaboralResumenDTO dto) {
        if (dto == null) return null;
        OportunidadLaboral entity = new OportunidadLaboral();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
        entity.setUbicacion(dto.getUbicacion());
        entity.setModalidad(dto.getModalidad());
        entity.setRangoSalarial(dto.getRangoSalarial());
        entity.setActiva(dto.isActiva());
        return entity;
    }
}
