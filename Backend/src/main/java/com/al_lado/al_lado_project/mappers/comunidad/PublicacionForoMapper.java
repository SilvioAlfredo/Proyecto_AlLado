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
        dto.setSector(entity.getSector());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setAutor(usuarioResumenMapper.toDTO(entity.getAutor()));
        return dto;
    }

    // La relacion autor se resuelve en PublicacionForoServiceImpl por ID;
    // no se reconstruye aqui para no crear entidades transient.
    // MEJ-003 / AL-SEC-008: Se fuerza entity.setId(null) para impedir sobrescrituras en altas
    public PublicacionForo toEntity(PublicacionForoDTO dto) {
        if (dto == null) return null;

        PublicacionForo entity = new PublicacionForo();
        entity.setId(null);
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
        entity.setSector(dto.getSector());
        return entity;
    }

    public void updateEntityFromDTO(PublicacionForoDTO dto, PublicacionForo entity) {
        if (dto == null || entity == null) return;
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
        entity.setSector(dto.getSector());
    }
}