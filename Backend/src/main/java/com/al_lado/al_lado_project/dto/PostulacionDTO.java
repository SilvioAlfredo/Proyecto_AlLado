package com.al_lado.al_lado_project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostulacionDTO extends PostBaseDTO {

    private Double presupuesto;
    private String disponibilidad;
    private String estado; // PENDIENTE, SELECCIONADA, RECHAZADA

    // 3. Rompemos el bucle usando el DTO resumen que creamos antes
    private OportunidadLaboralResumenDTO oportunidad;
}