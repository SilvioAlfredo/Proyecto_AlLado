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
    private String sitioWeb;
    private String urlLogo;
    private boolean verificado;
    private UsuarioResumenDTO empleador;
    private UsuarioResumenDTO supervisadoPor;
}