package com.al_lado.al_lado_project.dto.trabajo;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostulacionResumenDTO {

    private Long id;
    private String titulo;
    private String estado;
    private Double remuneracionPretendida;
    private LocalDateTime fechaCreacion;
    private UsuarioResumenDTO autor;
}