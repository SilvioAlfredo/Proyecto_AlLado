package com.al_lado.al_lado_project.dto.perfil;

import lombok.Data;

@Data
public class PreferenciaNotificacionDTO {

    private Long id;
    private boolean notificarEmail;
    private boolean notificarPush;
    private boolean alertasNuevasOfertas;
}