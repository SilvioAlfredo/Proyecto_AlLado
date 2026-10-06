package com.al_lado.al_lado_project.dto.trabajo;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaCreacion;
}