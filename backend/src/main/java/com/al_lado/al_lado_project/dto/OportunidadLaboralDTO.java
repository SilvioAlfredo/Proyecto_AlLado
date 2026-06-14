package com.al_lado.al_lado_project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data // 1. Genera getters, setters, toString, etc. automáticamente
@EqualsAndHashCode(callSuper = true) // 2. Incluye los atributos de PostBaseDTO en equals/hashCode
public class OportunidadLaboralDTO extends PostBaseDTO {

    private String estado; // Ejemplo: ACTIVA, CANCELADA, CERRADA
    private Date fechaPublicacion;
    private Date fechaCierre;

    // 3. Usamos un ResumenDTO en lugar del DTO completo para romper la recursión
    private List<PostulacionResumenDTO> postulacionesRecibidas = new ArrayList<>();
}