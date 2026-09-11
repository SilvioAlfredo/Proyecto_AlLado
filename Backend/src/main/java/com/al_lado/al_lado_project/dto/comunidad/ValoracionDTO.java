package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ValoracionDTO {

    private Long id;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private UsuarioResumenDTO autor;
    private UsuarioResumenDTO trabajador;
}