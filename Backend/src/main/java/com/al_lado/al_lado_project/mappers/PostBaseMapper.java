package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.PostBaseDTO;
import com.al_lado.al_lado_project.entity.PostBase;

public abstract class PostBaseMapper {

    // Cambiamos la lógica: en lugar de hacer 'new', pasamos el DTO ya creado por la clase hija
    protected void mapBaseToDTO(PostBase entity, PostBaseDTO dto) {
        if (entity == null || dto == null) return;
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setImagen(entity.getImagen());
    }

    // Lo mismo para el camino inverso hacia la entidad
    protected void mapBaseToEntity(PostBaseDTO dto, PostBase entity) {
        if (dto == null || entity == null) return;
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setImagen(dto.getImagen());
    }
}