package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReporteDTO {

    private Long id;

    @NotBlank(message = "El motivo del reporte es obligatorio.")
    private String motivo;

    @NotBlank(message = "La descripción del reporte es obligatoria.")
    private String descripcion;

    private String estado;
    private LocalDateTime fechaCreacion;

    @NotNull(message = "El usuario reportante es obligatorio.")
    private UsuarioResumenDTO reportante;

    private UsuarioResumenDTO reportado;
    private UsuarioResumenDTO moderador;
}