package com.al_lado.al_lado_project.mappers.perfil;

import com.al_lado.al_lado_project.dto.perfil.PortafolioItemDTO;
import com.al_lado.al_lado_project.entity.perfil.PortafolioItem;
import org.springframework.stereotype.Component;

@Component
public class PortafolioItemMapper {

    public PortafolioItemDTO toDTO(PortafolioItem entity) {
        if (entity == null) return null;

        PortafolioItemDTO dto = new PortafolioItemDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setUrlArchivo(entity.getUrlArchivo());
        return dto;
    }

    public PortafolioItem toEntity(PortafolioItemDTO dto) {
        if (dto == null) return null;

        PortafolioItem entity = new PortafolioItem();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setUrlArchivo(dto.getUrlArchivo());
        return entity;
    }
}
