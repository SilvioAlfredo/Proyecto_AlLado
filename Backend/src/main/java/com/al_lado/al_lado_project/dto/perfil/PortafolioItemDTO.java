package com.al_lado.al_lado_project.dto.perfil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PortafolioItemDTO {

    private Long id;

    @NotBlank(message = "El título del trabajo es obligatorio.")
    @Size(max = 120, message = "El título no puede superar los 120 caracteres.")
    private String titulo;

    private String descripcion;
    private String urlArchivo;
}