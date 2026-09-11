package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReporteDTO {

    private Long id;
    private String motivo;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaCreacion;
    private UsuarioResumenDTO reportante;
    private UsuarioResumenDTO reportado;
    private UsuarioResumenDTO moderador;
}