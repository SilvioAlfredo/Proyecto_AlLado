package com.al_lado.al_lado_project.dto;

import java.util.ArrayList;
import java.util.List;


public class EmpleadorDTO extends UsuarioDTO {
    private List<OportunidadLaboralDTO> oportunidades = new ArrayList<>();
}