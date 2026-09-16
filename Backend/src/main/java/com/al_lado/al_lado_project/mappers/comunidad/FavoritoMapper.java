package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.FavoritoDTO;
import com.al_lado.al_lado_project.entity.comunidad.Favorito;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoritoMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public FavoritoDTO toDTO(Favorito entity) {
        if (entity == null) return null;

        FavoritoDTO dto = new FavoritoDTO();
        dto.setId(entity.getId());
        dto.setEmpleador(usuarioResumenMapper.toDTO(entity.getEmpleador()));
        dto.setTrabajador(usuarioResumenMapper.toDTO(entity.getTrabajador()));
        dto.setFechaGuardado(entity.getFechaGuardado());
        return dto;
    }
}
