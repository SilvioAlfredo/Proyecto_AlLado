package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicacionForoDTO {

    private Long id;

    @NotBlank(message = "El título de la publicación es obligatorio.")
    @Size(max = 150, message = "El título no debe exceder los 150 caracteres.")
    private String titulo;

    @NotBlank(message = "El contenido no puede estar vacío.")
    private String contenido;

    private LocalDateTime fechaCreacion;

    @NotNull(message = "El autor es obligatorio.")
    private UsuarioResumenDTO autor;
}