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
        return dto;
    }

    public OportunidadLaboral toEntity(OportunidadLaboralResumenDTO dto) {
        if (dto == null) return null;
        OportunidadLaboral entity = new OportunidadLaboral();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        return entity;
    }
}
