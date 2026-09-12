package com.al_lado.al_lado_project.dto;

import lombok.Data;

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
public class PostulacionResumenDTO {

    private Long id;
    private Double presupuesto;
    private String disponibilidad;
    private String estado; // PENDIENTE, SELECCIONADA, RECHAZADA

    // Incluimos información básica del trabajador que postula
    // sin traer toda la lista de sus postulaciones de vuelta
    private UsuarioResumenDTO autor;
}