package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.SuscripcionDTO;
import com.al_lado.al_lado_project.entity.usuario.Suscripcion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.al_lado.al_lado_project.service.usuario.SuscripcionVigencia;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SuscripcionMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;
    private final Clock clock;

    public SuscripcionDTO toDTO(Suscripcion entity) {
        if (entity == null) return null;

        SuscripcionDTO dto = new SuscripcionDTO();
        dto.setId(entity.getId());
        dto.setNivel(entity.getNivel());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setActiva(entity.isActiva());
        LocalDate hoy = LocalDate.now(clock);
        dto.setVigente(SuscripcionVigencia.esVigente(entity, hoy));
        dto.setUsuario(usuarioResumenMapper.toDTO(entity.getUsuario()));
        return dto;
    }

    public Suscripcion toEntity(SuscripcionDTO dto) {
        if (dto == null) return null;

        Suscripcion entity = new Suscripcion();
        entity.setId(dto.getId());
        entity.setNivel(dto.getNivel());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setActiva(dto.isActiva());
        return entity;
    }
}
