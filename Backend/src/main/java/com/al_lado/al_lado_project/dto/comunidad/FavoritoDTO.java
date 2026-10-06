package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoritoDTO {

    private Long id;

    @NotNull(message = "El empleador es obligatorio.")
    private UsuarioResumenDTO empleador;

    @NotNull(message = "El trabajador guardado es obligatorio.")
    private UsuarioResumenDTO trabajador;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaGuardado;
}