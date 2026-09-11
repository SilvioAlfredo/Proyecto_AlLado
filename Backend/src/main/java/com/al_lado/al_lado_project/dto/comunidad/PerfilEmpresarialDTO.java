package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;

@Data
public class PerfilEmpresarialDTO {

    private Long id;
    private String nombreEmpresa;
    private String cuit;
    private String descripcion;
    private String sitioWeb;
    private String urlLogo;
    private boolean verificado;
    private UsuarioResumenDTO empleador;
    private UsuarioResumenDTO supervisadoPor;
}