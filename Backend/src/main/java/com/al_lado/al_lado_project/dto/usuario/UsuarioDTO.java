package com.al_lado.al_lado_project.dto.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public abstract class UsuarioDTO {

    private Long id;
    private String nombre;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}