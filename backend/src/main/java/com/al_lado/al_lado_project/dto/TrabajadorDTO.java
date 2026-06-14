package com.al_lado.al_lado_project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data // 1. Genera automáticamente getters, setters, toString, etc.
@EqualsAndHashCode(callSuper = true) // 2. Incluye los campos heredados de UsuarioDTO en equals y hashCode
public class TrabajadorDTO extends UsuarioDTO {

    // 3. Cambiamos a PostulacionResumenDTO para romper el bucle infinito
    private List<PostulacionResumenDTO> postulaciones = new ArrayList<>();
}