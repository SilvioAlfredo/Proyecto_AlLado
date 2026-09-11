package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class OportunidadLaboralDTO extends PostBaseDTO {

    private String ubicacion;
    private String modalidad;
    private String rangoSalarial;
    private boolean activa;

    private Set<EtiquetaDTO> etiquetas = new HashSet<>();
    private List<PostulacionResumenDTO> postulaciones = new ArrayList<>();
}