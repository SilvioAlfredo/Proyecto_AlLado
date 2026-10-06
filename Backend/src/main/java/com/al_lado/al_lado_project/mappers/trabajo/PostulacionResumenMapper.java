package com.al_lado.al_lado_project.mappers.trabajo;

import com.al_lado.al_lado_project.dto.trabajo.PostulacionResumenDTO;
import com.al_lado.al_lado_project.entity.trabajo.Postulacion;
import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostulacionResumenMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public PostulacionResumenDTO toDTO(Postulacion entity) {
        if (entity == null) return null;
        PostulacionResumenDTO dto = new PostulacionResumenDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setEstado(entity.getEstado());
        dto.setRemuneracionPretendida(entity.getRemuneracionPretendida());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setAutor(usuarioResumenMapper.toDTO(entity.getAutor()));
        return dto;
    }

    public Postulacion toEntity(PostulacionResumenDTO dto) {
        if (dto == null) return null;
        Postulacion entity = new Postulacion();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setEstado(dto.getEstado());
        entity.setRemuneracionPretendida(dto.getRemuneracionPretendida());
        return entity;
    }
}
