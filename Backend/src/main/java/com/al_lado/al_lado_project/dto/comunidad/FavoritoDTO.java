package com.al_lado.al_lado_project.dto.comunidad;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoritoDTO {

    private Long id;
    private UsuarioResumenDTO empleador;
    private UsuarioResumenDTO trabajador;
    private LocalDateTime fechaGuardado;
}