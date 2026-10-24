package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuscripcionMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public SuscripcionDTO toDTO(Suscripcion entity) {
        if (entity == null) return null;

        SuscripcionDTO dto = new SuscripcionDTO();
        dto.setId(entity.getId());
        dto.setNivel(entity.getNivel());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setActiva(entity.isActiva());
        dto.setUsuario(usuarioResumenMapper.toDTO(entity.getUsuario()));
        return dto;
    }

    public Suscripcion toEntity(SuscripcionDTO dto) {
        if (dto == null) return null;

        Suscripcion entity = new Suscripcion();
        // MEJ-003 / AL-SEC-008: el ID que manda el cliente no se copia en un alta.
        // Sólo la base de datos asigna IDs, de modo que ninguna petición puede
        // apuntar el alta a un registro existente y sobrescribirlo.
        entity.setId(null);
        entity.setNivel(dto.getNivel());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setActiva(dto.isActiva());
        return entity;
    }
}
