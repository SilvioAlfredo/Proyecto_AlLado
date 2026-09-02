package com.al_lado.al_lado_project.dto;

import lombok.Data;
import java.util.Date;

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
public class OportunidadLaboralResumenDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String imagen; // Ruta o URL de la imagen
    private String estado; // Ejemplo: ACTIVA, CANCELADA, CERRADA
    private Date fechaPublicacion;
    private Date fechaCierre;
}