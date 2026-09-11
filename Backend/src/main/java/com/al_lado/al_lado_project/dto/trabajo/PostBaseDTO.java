package com.al_lado.al_lado_project.dto.trabajo;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class PostBaseDTO {

    private Long id;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private UsuarioResumenDTO autor;
}