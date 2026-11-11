package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.ComunicadoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Comunicado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComunicadoMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public ComunicadoDTO toDTO(Comunicado entity) {
        if (entity == null) return null;

        ComunicadoDTO dto = new ComunicadoDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setAlcance(entity.getAlcance());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setEmisor(usuarioResumenMapper.toDTO(entity.getEmisor()));
        return dto;
    }

    // La relacion emisor se resuelve en ComunicadoServiceImpl por ID;
    // no se reconstruye aqui para no crear entidades transient.
    public Comunicado toEntity(ComunicadoDTO dto) {
        if (dto == null) return null;

        Comunicado entity = new Comunicado();
        entity.setId(dto.getId());
        entity.setTitulo(dto.getTitulo());
        entity.setContenido(dto.getContenido());
        entity.setAlcance(dto.getAlcance());
        return entity;
    }
}
