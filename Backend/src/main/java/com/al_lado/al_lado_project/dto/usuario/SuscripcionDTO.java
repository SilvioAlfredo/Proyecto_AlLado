package com.al_lado.al_lado_project.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SuscripcionDTO {

    private Long id;

    @NotNull(message = "El usuario es obligatorio para la suscripción.")
    private UsuarioResumenDTO usuario;

    @NotBlank(message = "El nivel de suscripción es obligatorio.")
    private String nivel;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;
    private boolean activa;
}