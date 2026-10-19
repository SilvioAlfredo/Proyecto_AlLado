package com.al_lado.al_lado_project.dto.trabajo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class OportunidadLaboralDTO extends PostBaseDTO {

    @Size(max = 100, message = "La ubicación no puede superar los 100 caracteres.")
    private String ubicacion;
    @Size(max = 50, message = "La modalidad no puede superar los 50 caracteres.")
    @Pattern(regexp = "PRESENCIAL|REMOTO|HIBRIDO", message = "Modalidad inválida. Valores permitidos: PRESENCIAL, REMOTO, HIBRIDO.")
    private String modalidad;
    @Size(max = 100, message = "El rango salarial no puede superar los 100 caracteres.")
    private String rangoSalarial;
    private boolean activa;
    private boolean destacada;

    private Set<EtiquetaDTO> etiquetas = new HashSet<>();
    private List<PostulacionResumenDTO> postulaciones = new ArrayList<>();
}