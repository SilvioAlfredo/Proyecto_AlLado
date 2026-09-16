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
        return dto;
    }
}
