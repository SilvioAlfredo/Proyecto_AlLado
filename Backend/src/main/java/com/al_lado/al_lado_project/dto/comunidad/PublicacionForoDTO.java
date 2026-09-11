package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PublicacionForoDTO {

    private Long id;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private UsuarioResumenDTO autor;
}