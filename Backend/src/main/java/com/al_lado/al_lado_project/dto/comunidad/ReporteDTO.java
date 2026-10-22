package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReporteDTO {

    private Long id;

    @NotBlank(message = "El motivo del reporte es obligatorio.")
    private String motivo;

    @NotBlank(message = "La descripción del reporte es obligatoria.")
    private String descripcion;

    @Size(max = 100, message = "El sector no debe exceder los 100 caracteres.")
    private String sector;

    @Pattern(regexp = "PENDIENTE|EN_REVISION|RESUELTO|DESCARTADO", message = "Estado inválido. Valores permitidos: PENDIENTE, EN_REVISION, RESUELTO, DESCARTADO.")
    private String estado;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaCreacion;

    @NotNull(message = "El usuario reportante es obligatorio.")
    private UsuarioResumenDTO reportante;

    private UsuarioResumenDTO reportado;
    private UsuarioResumenDTO moderador;
}