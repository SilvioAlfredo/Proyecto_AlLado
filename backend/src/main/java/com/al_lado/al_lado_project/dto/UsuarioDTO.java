package com.al_lado.al_lado_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data // 1. Genera automáticamente getters, setters, toString, etc.
public abstract class UsuarioDTO {

    private Long id; //
    private String nombre; //
    private String email; //

    // 2. Controla la visibilidad de la contraseña en el JSON
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; //
}