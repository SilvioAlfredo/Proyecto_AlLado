package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import org.springframework.stereotype.Component;

@Component
public class OportunidadLaboralMapper {

    public OportunidadLaboralDTO toDTO(OportunidadLaboral entity) {
        if (entity == null) return null;
        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }

    public OportunidadLaboral toEntity(OportunidadLaboralDTO dto) {
        if (dto == null) return null;
        OportunidadLaboral entity = new OportunidadLaboral();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }
}
