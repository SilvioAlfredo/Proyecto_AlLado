package com.al_lado.al_lado_project.dto;

import jakarta.persistence.*;

public class PostulacionDTO extends PostBaseDTO {

    private Double presupuesto;
    private String disponibilidad;
    private String estado; // PENDIENTE, SELECCIONADA, RECHAZADA
    private OportunidadLaboralDTO oportunidad;
}