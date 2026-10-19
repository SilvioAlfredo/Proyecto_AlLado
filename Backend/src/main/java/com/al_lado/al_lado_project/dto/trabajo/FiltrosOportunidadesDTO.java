package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;

import java.util.List;

@Data
public class FiltrosOportunidadesDTO {
    private String filtro;
    private String modalidad;
    private List<String> etiquetas;
    private Boolean soloDestacadas;
}
