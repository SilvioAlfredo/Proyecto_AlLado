package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostulacionDTO extends PostBaseDTO {

    private String estado;
    private Double remuneracionPretendida;
    private OportunidadLaboralResumenDTO oportunidadLaboral;
}