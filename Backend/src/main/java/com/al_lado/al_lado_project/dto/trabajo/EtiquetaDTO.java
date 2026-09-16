package com.al_lado.al_lado_project.dto.trabajo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EtiquetaDTO {

    private Long id;

    @NotBlank(message = "El nombre de la etiqueta es obligatorio.")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres.")
    private String nombre;
}