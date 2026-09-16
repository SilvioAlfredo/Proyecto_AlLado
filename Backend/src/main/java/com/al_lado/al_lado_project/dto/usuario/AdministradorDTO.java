package com.al_lado.al_lado_project.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdministradorDTO extends UsuarioDTO {

    @NotBlank(message = "El nivel de acceso es obligatorio.")
    private String nivelAcceso;
}