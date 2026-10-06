package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostBaseDTO;
import com.al_lado.al_lado_project.entity.trabajo.PostBase;

public abstract class PostBaseMapper {

    protected void mapBaseToDTO(PostBase entity, PostBaseDTO dto) {
        if (entity == null || dto == null) return;
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setFechaCreacion(entity.getFechaCreacion());
    }

    protected void mapBaseToEntity(PostBaseDTO dto, PostBase entity) {
        if (dto == null || entity == null) return;
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
    }
}