package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.PublicacionForoDTO;
import com.al_lado.al_lado_project.entity.comunidad.PublicacionForo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicacionForoMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public PublicacionForoDTO toDTO(PublicacionForo entity) {
        if (entity == null) return null;

        PublicacionForoDTO dto = new PublicacionForoDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setAutor(usuarioResumenMapper.toDTO(entity.getAutor()));
        return dto;
    }

    public PublicacionForo toEntity(PublicacionForoDTO dto) {
        if (dto == null) return null;

        PublicacionForo entity = new PublicacionForo();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
        return entity;
    }
}
