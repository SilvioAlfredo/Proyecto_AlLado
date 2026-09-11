package com.al_lado.al_lado_project.dto.usuario;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdministradorDTO extends UsuarioDTO {

    private String nivelAcceso;
}