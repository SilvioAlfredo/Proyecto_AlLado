package com.al_lado.al_lado_project.mappers.perfil;

import com.al_lado.al_lado_project.dto.perfil.PreferenciaNotificacionDTO;
import com.al_lado.al_lado_project.entity.perfil.PreferenciaNotificacion;
import org.springframework.stereotype.Component;

@Component
public class PreferenciaNotificacionMapper {

    public PreferenciaNotificacionDTO toDTO(PreferenciaNotificacion entity) {
        if (entity == null) return null;

        PreferenciaNotificacionDTO dto = new PreferenciaNotificacionDTO();
        dto.setId(entity.getId());
        dto.setNotificarEmail(entity.isNotificarEmail());
        dto.setNotificarPush(entity.isNotificarPush());
        dto.setAlertasNuevasOfertas(entity.isAlertasNuevasOfertas());
        dto.setAlertasNuevasPostulaciones(entity.isAlertasNuevasPostulaciones());
        return dto;
    }

    // La relacion trabajador se resuelve en PreferenciaNotificacionServiceImpl
    // por trabajadorId; no se reconstruye aqui para no crear entidades transient.
    public PreferenciaNotificacion toEntity(PreferenciaNotificacionDTO dto) {
        if (dto == null) return null;

        PreferenciaNotificacion entity = new PreferenciaNotificacion();
        entity.setId(dto.getId());
        entity.setNotificarEmail(dto.isNotificarEmail());
        entity.setNotificarPush(dto.isNotificarPush());
        entity.setAlertasNuevasOfertas(dto.isAlertasNuevasOfertas());
        entity.setAlertasNuevasPostulaciones(dto.isAlertasNuevasPostulaciones());
        return entity;
    }
}
