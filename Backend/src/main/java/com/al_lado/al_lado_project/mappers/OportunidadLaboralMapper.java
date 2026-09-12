package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.OportunidadLaboralDTO;
import com.al_lado.al_lado_project.entity.trabajo.OportunidadLaboral;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;

@Component
public class OportunidadLaboralMapper extends PostBaseMapper {

    public OportunidadLaboralDTO toDTO(OportunidadLaboral entity) {
        if (entity == null) return null;

        OportunidadLaboralDTO dto = new OportunidadLaboralDTO();
        mapBaseToDTO(entity, dto);

        dto.setEstado(entity.isActiva() ? "ACTIVA" : "CERRADA");

        if (entity.getFechaCreacion() != null) {
            dto.setFechaPublicacion(Date.from(entity.getFechaCreacion().atZone(ZoneId.systemDefault()).toInstant()));
        }

        return dto;
    }

    public OportunidadLaboral toEntity(OportunidadLaboralDTO dto) {
        if (dto == null) return null;

        OportunidadLaboral entity = new OportunidadLaboral();
        mapBaseToEntity(dto, entity);

        entity.setActiva(dto.getEstado() == null || !"CERRADA".equalsIgnoreCase(dto.getEstado()));

        return entity;
    }
}