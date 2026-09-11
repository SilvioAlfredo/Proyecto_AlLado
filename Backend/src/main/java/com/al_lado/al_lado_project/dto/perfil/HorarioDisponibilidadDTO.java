package com.al_lado.al_lado_project.dto.perfil;

import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioDisponibilidadDTO {

    private Long id;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}