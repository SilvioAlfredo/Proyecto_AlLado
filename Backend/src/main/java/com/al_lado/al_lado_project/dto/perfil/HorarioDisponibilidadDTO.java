package com.al_lado.al_lado_project.dto.perfil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioDisponibilidadDTO {

    private Long id;

    @NotBlank(message = "El día de la semana es obligatorio.")
    private String diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria.")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria.")
    private LocalTime horaFin;
}