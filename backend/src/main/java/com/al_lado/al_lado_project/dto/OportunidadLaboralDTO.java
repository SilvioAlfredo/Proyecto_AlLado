package com.al_lado.al_lado_project.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OportunidadLaboralDTO extends PostBaseDTO {

    private String estado; // Ejemplo: ACTIVA, CANCELADA, CERRADA
    private Date fechaPublicacion;
    private Date fechaCierre;
    private List<PostulacionDTO> postulacionesRecibidas = new ArrayList<>();
}