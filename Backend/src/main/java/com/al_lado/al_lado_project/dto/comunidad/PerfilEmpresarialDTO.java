package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilEmpresarialDTO {

    private Long id;

    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String nombreEmpresa;

    @Size(max = 20, message = "El CUIT no puede superar los 20 caracteres.")
    private String cuit;

    private String descripcion;
    @Size(max = 150, message = "El sitio web no puede superar los 150 caracteres.")
    private String sitioWeb;
    @Size(max = 255, message = "La URL del logo no puede superar los 255 caracteres.")
    private String urlLogo;
    private boolean verificado;
    private UsuarioResumenDTO empleador;
    private UsuarioResumenDTO supervisadoPor;
}