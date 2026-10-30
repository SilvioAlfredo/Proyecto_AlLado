package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;

import java.util.List;

/** Resumen de un trabajador ordenado por compatibilidad con una oportunidad. */
@Data
public class CandidatoMatchingDTO {

    private Long trabajadorId;
    private String nombre;
    private String sector;
    private int puntuacion;
    private double distanciaKm;
    private List<String> especialidadesCoincidentes;
    private boolean dentroDelRadio;
    private boolean disponibilidadDeclarada;
    private boolean disponibilidadCoincidente;
    private boolean horarioRequerido;
}
