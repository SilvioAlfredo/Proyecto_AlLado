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

    // Las relaciones empleador/trabajador se resuelven en FavoritoServiceImpl
    // a partir de sus IDs (ver agregar(empleadorId, trabajadorId)); no se
    // reconstruyen aquí para no crear entidades transient.
    public Favorito toEntity(FavoritoDTO dto) {
        if (dto == null) return null;

        Favorito entity = new Favorito();
        entity.setId(dto.getId());
        return entity;
    }
}
