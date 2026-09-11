package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OportunidadLaboralResumenDTO {

    private Long id;
    private String titulo;
    private String contenido;
    private String ubicacion;
    private String modalidad;
    private String rangoSalarial;
    private boolean activa;
    private LocalDateTime fechaCreacion;
}