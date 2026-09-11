package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PublicidadDTO {

    private Long id;
    private String titulo;
    private String contenido;
    private String urlImagen;
    private String urlDestino;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;
    private UsuarioResumenDTO anunciante;
}