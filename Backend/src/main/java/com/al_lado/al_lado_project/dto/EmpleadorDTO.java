package com.al_lado.al_lado_project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data // 1. Genera automáticamente getters, setters, toString, equals y hashCode
@EqualsAndHashCode(callSuper = true) // 2. Incluye los campos de UsuarioDTO en el equals/hashCode
public class EmpleadorDTO extends UsuarioDTO {

    // 3. OportunidadLaboralResumenDTO evita la recursión infinita
    private List<OportunidadLaboralResumenDTO> oportunidades = new ArrayList<>();
}