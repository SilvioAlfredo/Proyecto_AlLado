package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.OportunidadLaboral;
import com.al_lado.al_lado_project.entity.Usuario;
import com.al_lado.al_lado_project.entity.Empleador;
import org.springframework.stereotype.Component;

@Component
public class OportunidadLaboralMapper extends PostBaseMapper { // <-- Extiende de PostBaseMapper

    public OportunidadLaboralDTO toDTO(OportunidadLaboral entity) {
        if (entity == null) return null;
        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        mapBaseToDTO(entity, dto); // <-- Mapea id, titulo, descripcion e imagen automáticamente
        dto.setEstado(entity.getEstado());
        dto.setFechaPublicacion(entity.getFechaPublicacion());
        dto.setFechaCierre(entity.getFechaCierre());
        return dto;
    }

    public OportunidadLaboral toEntity(OportunidadLaboralDTO dto) {
        if (dto == null) return null;
        OportunidadLaboral entity = new OportunidadLaboral();
        mapBaseToEntity(dto, entity); // <-- Mapea los campos base hacia la entidad
        entity.setEstado(dto.getEstado());
        entity.setFechaPublicacion(dto.getFechaPublicacion());
        entity.setFechaCierre(dto.getFechaCierre());
        return entity;
    }
}