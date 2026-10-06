package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComunicadoDTO {

    private Long id;

    @NotBlank(message = "El título del comunicado es obligatorio.")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres.")
    private String titulo;

    @NotBlank(message = "El contenido no puede estar vacío.")
    private String contenido;

    private String alcance;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaEmision;

    @NotNull(message = "El emisor es obligatorio.")
    private UsuarioResumenDTO emisor;
}