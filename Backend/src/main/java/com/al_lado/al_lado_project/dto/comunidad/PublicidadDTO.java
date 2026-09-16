package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PublicidadDTO {

    private Long id;

    @NotBlank(message = "El título publicitario es obligatorio.")
    @Size(max = 120, message = "El título no debe exceder los 120 caracteres.")
    private String titulo;

    private String contenido;
    private String urlImagen;
    private String urlDestino;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria.")
    private LocalDate fechaFin;

    private boolean activa;

    @NotNull(message = "El anunciante es obligatorio.")
    private UsuarioResumenDTO anunciante;
}