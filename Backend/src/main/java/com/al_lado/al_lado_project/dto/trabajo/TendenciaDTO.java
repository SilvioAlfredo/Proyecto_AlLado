package com.al_lado.al_lado_project.dto.trabajo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TendenciaDTO {
    private String nombre;
    private long cantidad;
    private int porcentaje;
    private String colorClass;
}