package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.PostulacionResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import org.springframework.stereotype.Component;

@Component
public class PostulacionResumenMapper {

    public PostulacionResumenDTO toDTO(Postulacion entity) {
        if (entity == null) return null;
        PostulacionResumenDTO dto = new PostulacionResumenDTO();
        dto.setId(entity.getId());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    public Postulacion toEntity(PostulacionResumenDTO dto) {
        if (dto == null) return null;
        Postulacion entity = new Postulacion();
        entity.setId(dto.getId());
        entity.setEstado(dto.getEstado());
        return entity;
    }
}
