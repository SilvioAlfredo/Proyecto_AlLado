package com.al_lado.al_lado_project.service.perfil;

import com.al_lado.al_lado_project.dto.perfil.PreferenciaNotificacionDTO;

public interface PreferenciaNotificacionService {
    PreferenciaNotificacionDTO obtenerPorTrabajador(Long trabajadorId);
    PreferenciaNotificacionDTO guardar(Long trabajadorId, PreferenciaNotificacionDTO dto);
}
