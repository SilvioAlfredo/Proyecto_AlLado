package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.ValoracionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Valoracion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValoracionMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public ValoracionDTO toDTO(Valoracion entity) {
        if (entity == null) return null;

        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(entity.getId());
        dto.setPuntuacion(entity.getPuntuacion());
        dto.setComentario(entity.getComentario());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setAutor(usuarioResumenMapper.toDTO(entity.getAutor()));
        dto.setTrabajador(usuarioResumenMapper.toDTO(entity.getTrabajador()));
        return dto;
    }

    // Las relaciones autor/trabajador se resuelven en ValoracionServiceImpl
    // por ID; no se reconstruyen aqui para no crear entidades transient.
    public Valoracion toEntity(ValoracionDTO dto) {
        if (dto == null) return null;

        Valoracion entity = new Valoracion();
        entity.setId(dto.getId());
        entity.setPuntuacion(dto.getPuntuacion());
        entity.setComentario(dto.getComentario());
        return entity;
    }
}
